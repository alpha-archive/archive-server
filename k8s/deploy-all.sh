#!/bin/bash

echo "🚀 Deploying all services to archive namespace..."

# 1. PostgreSQL 관련 리소스 배포
echo "📊 Deploying PostgreSQL..."
kubectl apply -f postgresql-configmap.yaml
kubectl apply -f postgresql-secret.yaml
kubectl apply -f postgresql-pvc.yaml
kubectl apply -f postgresql-deployment.yaml
kubectl apply -f postgresql-service.yaml

# 2. Redis 배포
echo "🔴 Deploying Redis..."
kubectl apply -f redis-deployment.yaml
kubectl apply -f redis-service.yaml

# 3. Archive API 배포
echo "🛠️  Deploying Archive API..."
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

echo "✅ Deployment completed!"
echo ""
echo "📋 Checking deployment status:"
kubectl get pods -n archive
echo ""
echo "🌐 Services:"
kubectl get svc -n archive
