# Archive Kubernetes Deployment

## 📁 파일 구조

### 🚀 메인 애플리케이션
- **`deployment.yaml`** - Archive API 파드 배포 설정
- **`service.yaml`** - LoadBalancer 서비스 (HTTP 접근용)

### 🗄️ PostgreSQL
- **`postgresql-configmap.yaml`** - 데이터베이스 설정
- **`postgresql-secret.yaml`** - 데이터베이스 비밀번호
- **`postgresql-pvc.yaml`** - 데이터 저장소
- **`postgresql-deployment.yaml`** - PostgreSQL 파드
- **`postgresql-service.yaml`** - PostgreSQL 서비스

### 🔴 Redis
- **`redis-deployment.yaml`** - Redis 파드
- **`redis-service.yaml`** - Redis 서비스

### 🛠️ 배포 스크립트
- **`deploy-all.sh`** - 모든 리소스 배포 스크립트

## 🚀 배포 방법

### 전체 배포
```bash
chmod +x deploy-all.sh
./deploy-all.sh
```

### 개별 배포
```bash
# 데이터베이스 먼저
kubectl apply -f postgresql-configmap.yaml
kubectl apply -f postgresql-secret.yaml
kubectl apply -f postgresql-pvc.yaml
kubectl apply -f postgresql-deployment.yaml
kubectl apply -f postgresql-service.yaml

# Redis
kubectl apply -f redis-deployment.yaml
kubectl apply -f redis-service.yaml

# API 서비스
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```

## 🌐 접속 정보

### HTTP 접속 (현재 작동 중)
```
http://archive-archive-api-serv-c1bfd-108259986-b6cce380c931.kr.lb.naverncp.com
```

### 주요 엔드포인트
- **헬스체크**: `/actuator/health`
- **API 문서**: `/swagger-ui/index.html`
- **API**: `/api/*`

## 📊 상태 확인

```bash
# 파드 상태
kubectl get pods -n archive

# 서비스 상태
kubectl get svc -n archive

# 로그 확인
kubectl logs -n archive -l app=archive-api
```

