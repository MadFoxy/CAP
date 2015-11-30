
# CAP
## CAP和PostgreSQL(DB)docker镜像 

CAP `docker/cap`

1. 基于Dockerfile构建CAP镜像,其中 `lee5hxi/cap` 替换为自己的docker hub :

	```bash
	sudo docker build --rm -t="lee5hxi/cap" .
	```

2. 基于CAP镜像运行容器,其中`/home/lee5hx/java_works/lee5hx/CAP` 替换为自己CAP目录 :

	```bash
	sudo docker run --name cap-run -d -v /home/lee5hx/java_works/lee5hx/CAP:/CAP lee5hxi/cap /bin/bash
	```
3. 启动与关闭容器:

	```bash
	sudo docker start cap-run
	```
	```bash
	sudo docker stop cap-run
	```


PostgreSQL(DB) `docker/postgres`

1. 基于Dockerfile构建Postgres镜像,其中 `lee5hxi/postgres` 替换为自己的docker hub :

	```bash
	sudo docker build --rm -t="lee5hxi/postgres" .
	```

2. 基于Postgres镜像运行容器:

	```bash
	sudo docker run --name cap-db -e POSTGRES_PASSWORD=postgres -d -p 5432 lee5hxi/postgres
	```
3. 启动与关闭容器:

	```bash
	sudo docker start cap-db
	```
	```bash
	sudo docker stop cap-db
	```

