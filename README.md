
# CAP
## CAP and postgres docker images 

Docker镜像由 CAP与postgres(DB)组成。

1. 基于Dockerfile构建Postgres镜像,其中 `lee5hxi/postgres` 替换为自己的docker hub :

	```bash
	sudo docker build --rm -t="lee5hxi/postgres" .
	```

2. Postgres镜像运行容器:

	```bash
	sudo docker run --name some-postgres -e POSTGRES_PASSWORD=postgres -d -p 5432 lee5hxi/postgres
	```
3. 启动与关闭容器:

	```bash
	sudo docker start some-postgres
	```
	```bash
	sudo docker stop some-postgres
	```

