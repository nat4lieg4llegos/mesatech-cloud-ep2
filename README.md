# MesaTech Cloud · EP2 · DSY1107 Desarrollo Cloud Native I

Catálogo crea categorías y publica un evento en RabbitMQ; Solicitudes lo consume y guarda una copia en su propia RDS para usarla al registrar solicitudes.

```
POST categoría → ms-catalogo (EC2 A) → RDS Catálogo
                      └→ catalogo.exchange ──categoria.creada──→ solicitudes.catalogo.queue  (RabbitMQ, EC2 C)
                                                                        └→ ms-solicitudes (EC2 B) → RDS Solicitudes
```

## Estructura

| Carpeta | Contenido |
|---|---|
| `ms-catalogo/` | Spring Boot, publicador. Puerto 8081. EC2 A |
| `ms-solicitudes/` | Spring Boot, consumidor. Puerto 8082. EC2 B |
| `rabbitmq/` | `compose.yaml`, `.env.example`, definiciones (vhost, exchange, cola, binding) y script de usuarios/permisos. EC2 C |
| `sql/` | Bases, usuarios de aplicación y tablas de cada RDS |
| `deploy/` | Servicio systemd para los microservicios |
| `docs/` | Contrato del evento |

## Versiones

| Componente | Versión |
|---|---|
| Java | 21 (Temurin / OpenJDK) |
| Spring Boot | 4.1.1 |
| RabbitMQ | imagen oficial `rabbitmq:4.2-management` |
| Docker Engine / Compose plugin | completar con `docker --version` y `docker compose version` |
| SO EC2 C | Ubuntu 24.04 LTS |
| RDS | MySQL 8.x, puerto 3306 |

## Recursos AWS

| Recurso | Nombre sugerido | Qué corre |
|---|---|---|
| EC2 A | `mesatech-ec2-catalogo` | ms-catalogo (+ BFF) |
| EC2 B | `mesatech-ec2-solicitudes` | ms-solicitudes |
| EC2 C | `mesatech-ec2-rabbitmq` | RabbitMQ (Docker Compose) |
| RDS 1 | `mesatech-catalogo-db` | base `catalogo` |
| RDS 2 | `mesatech-solicitudes-db` | base `solicitudes` |

### Security groups

| Destino | Puerto | Origen |
|---|---|---|
| EC2 C | 5672 | SG de EC2 A y SG de EC2 B |
| EC2 C | 15672 | IP administrativa /32 |
| RDS Catálogo | 3306 | SG de EC2 A |
| RDS Solicitudes | 3306 | SG de EC2 B |
| EC2 A, B, C | 22 | IP administrativa /32 |
| EC2 A / B | 8081 / 8082 | según integración de la EP1 (BFF) |

RDS sin acceso público. Los microservicios se conectan a RabbitMQ por la **IP privada** de la EC2 C.

## 1. RabbitMQ (EC2 C)

```bash
cd ~/rabbitmq
cp .env.example .env && nano .env && chmod 600 .env
docker compose config --quiet      # valida el archivo (sin salida = OK)
docker compose pull                # descarga la imagen
docker compose up -d               # arranca
docker compose ps                  # estado (healthy)
docker compose logs --tail 50 rabbitmq
docker exec rabbitmq rabbitmq-diagnostics status
bash crear-recursos.sh             # vhost, exchange, cola, binding, usuarios y permisos
```

## 2. Bases de datos

Ejecutar `sql/01_rds_catalogo.sql` en la RDS de Catálogo **desde la EC2 A** y `sql/02_rds_solicitudes.sql` en la RDS de Solicitudes **desde la EC2 B**:

```bash
sudo apt install -y mysql-client
mysql -h <endpoint-rds> -u admin -p < 01_rds_catalogo.sql
```

## 3. Compilar (en el PC)

```bash
cd ms-catalogo   && ./mvnw clean package -DskipTests   # → target/ms-catalogo-1.0.0.jar
cd ms-solicitudes && ./mvnw clean package -DskipTests  # → target/ms-solicitudes-1.0.0.jar
```

En Windows: `mvnw.cmd clean package -DskipTests`.

## 4. Desplegar (EC2 A y EC2 B)

```bash
# desde el PC
scp -i llave.pem ms-catalogo/target/ms-catalogo-1.0.0.jar ubuntu@<IP-EC2-A>:/tmp/ms-catalogo.jar
scp -i llave.pem ms-catalogo/.env.example deploy/mesatech-ms.service ubuntu@<IP-EC2-A>:/tmp/

# en la EC2
sudo apt update && sudo apt install -y openjdk-21-jre-headless
sudo mkdir -p /opt/mesatech && sudo chown ubuntu:ubuntu /opt/mesatech
mv /tmp/ms-catalogo.jar /opt/mesatech/
cp /tmp/.env.example /opt/mesatech/.env && nano /opt/mesatech/.env && chmod 600 /opt/mesatech/.env
sed 's/NOMBRE_MS/ms-catalogo/g' /tmp/mesatech-ms.service | sudo tee /etc/systemd/system/ms-catalogo.service
sudo systemctl daemon-reload && sudo systemctl enable --now ms-catalogo
journalctl -u ms-catalogo -f      # logs
```

Igual en la EC2 B reemplazando `ms-catalogo` por `ms-solicitudes`.

## Arranque, detención y recuperación

Orden de arranque: **RDS → RabbitMQ (EC2 C) → ms-solicitudes (EC2 B) → ms-catalogo (EC2 A) → BFF**.

| Acción | Comando |
|---|---|
| Detener / iniciar microservicio | `sudo systemctl stop ms-solicitudes` / `start` |
| Estado | `systemctl status ms-catalogo` |
| Detener / iniciar broker (conserva volumen) | `docker compose stop` / `docker compose start` |
| Recrear contenedor (P5) | `docker compose up -d --force-recreate` (**nunca** `down -v`) |

## Endpoints

| Servicio | Método y ruta | Uso |
|---|---|---|
| ms-catalogo | `POST /api/v1/categorias` `{"nombre":"...","descripcion":"..."}` | 201 guardada y publicada · 409 nombre repetido · 503 guardada sin evento |
| ms-catalogo | `GET /api/v1/categorias` | categorías originales |
| ms-solicitudes | `GET /api/v1/categorias-disponibles` | copia local recibida por RabbitMQ |
| ms-solicitudes | `POST /api/v1/solicitudes` `{"titulo":"...","descripcion":"...","categoriaId":1}` | 201 · 422 si la categoría aún no llegó |
| ms-solicitudes | `GET /api/v1/solicitudes`, `GET /api/v1/solicitudes/{id}` | consulta |
| ms-solicitudes | `PATCH /api/v1/solicitudes/{id}/estado` `{"estado":"EN_PROCESO"}` | 422 si se intenta RESUELTA sin pasar por EN_PROCESO |

## Límites conocidos

Guardar en RDS y publicar son pasos separados: si RabbitMQ no confirma, la categoría queda en Catálogo pero no llega a Solicitudes (respuesta 503 con `categoriaGuardada=true`). Reenviar la solicitud daría error 409 por nombre repetido. Si un mensaje se entrega dos veces, el consumidor no duplica la copia (`categoria_id` único), pero no existe garantía de procesamiento exactamente una vez.
