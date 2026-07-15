# ============================================================
# Deploy Punto Venta al VPS
# Autor: Miguel
# ============================================================

$ErrorActionPreference = "Stop"

# ---------- Configuración ----------
$VPS_IP     = "168.197.49.177"
$VPS_PORT   = 5740
$VPS_USER   = "miguel"

$REMOTE_DIR = "/home/miguel/apps/punto-venta-docker"

$JAR_NAME = "punto-venta-0.0.1-SNAPSHOT.jar"

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "   DEPLOY PUNTO VENTA" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# ---------- Compilar ----------
Write-Host "[1/6] Compilando proyecto..." -ForegroundColor Yellow
# ---------- Compilación opcional ----------

$compilar = Read-Host "¿Desea compilar una nueva versión? (S/N)"

if ($compilar -eq "S" -or $compilar -eq "s") {

    Write-Host ""
    Write-Host "[1/6] Compilando proyecto..." -ForegroundColor Yellow

    mvn clean package -DskipTests

    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Falló la compilación." -ForegroundColor Red
        exit 1
    }

    Write-Host "OK - Compilación correcta." -ForegroundColor Green

}
else {

    Write-Host ""
    Write-Host "[1/6] Usando JAR existente..." -ForegroundColor Yellow

}

# ---------- Verificar Jar ----------
$jar = "target\$JAR_NAME"

if (!(Test-Path $jar)) {
    Write-Host ""
    Write-Host "ERROR: No se encontró $jar" -ForegroundColor Red
    exit 1
}

Write-Host "OK - Compilación correcta." -ForegroundColor Green

# ---------- Crear carpetas ----------
Write-Host ""
Write-Host "[2/6] Preparando VPS..." -ForegroundColor Yellow

# ssh -p $VPS_PORT "$VPS_USER@$VPS_IP" "mkdir -p /home/miguel/apps/punto-venta-docker/target"

#if ($LASTEXITCODE -ne 0) {
#    Write-Host "ERROR preparando el VPS." -ForegroundColor Red
#    exit 1
#} 

# ---------- Copiar archivos ----------
Write-Host ""
Write-Host "[3/6] Copiando archivos..." -ForegroundColor Yellow

scp -P $VPS_PORT Dockerfile "$VPS_USER@$VPS_IP`:$REMOTE_DIR/"
scp -P $VPS_PORT docker-compose.yml "$VPS_USER@$VPS_IP`:$REMOTE_DIR/"
scp -P $VPS_PORT .env.prod "$VPS_USER@$VPS_IP`:$REMOTE_DIR/"
scp -P $VPS_PORT .dockerignore "$VPS_USER@$VPS_IP`:$REMOTE_DIR/"
scp -P $VPS_PORT $jar "$VPS_USER@$VPS_IP`:$REMOTE_DIR/target/"

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR copiando archivos." -ForegroundColor Red
    exit 1
}

Write-Host "OK - Archivos copiados." -ForegroundColor Green



Write-Host ""
Write-Host "==========================================" -ForegroundColor Green
Write-Host " DEPLOY FINALIZADO CORRECTAMENTE" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""