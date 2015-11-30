
# CAP
## CAP and postgres docker images 

Docker镜像由 CAP与postgres(DB)组成。

1.	基于Dockerfile构建Postgres镜像:

	```bash
	sudo docker build --rm -t="lee5hxi/postgres" .
	```

2. :

	```bash
	sudo docker run --name some-postgres -e POSTGRES_PASSWORD=postgres -d -p 5432 lee5hxi/postgres
	CMD ["postgres"]
	```

	1.	Ensure that `docker run official-image bash` works too. The easiest way is to check for the expected command and if it is something else, just `exec "$@"` (run whatever was passed, properly keeping the arguments escaped).

		```bash
		#!/bin/bash
		set -e

		# this if will check if the first argument is a flag
		# but only works if all arguments require a hyphenated flag
		# -v; -SL; -f arg; etc will work, but not arg1 arg2
		if [ "${1:0:1}" = '-' ]; then
		    set -- mongod "$@"
		fi

		# check for the expected command
		if [ "$1" = 'mongod' ]; then
		    # init db stuff....
		    # use gosu to drop to a non-root user
		    exec gosu mongod "$@"
		fi

		# else default to run whatever the user wanted like "bash"
		exec "$@"
		```

3.	If the image only contains the main executable and its linked libraries (ie no shell) then it is fine to use the executable as the `ENTRYPOINT`, since that is the only thing that can run:

	```Dockerfile
	ENTRYPOINT ["swarm"]
	CMD ["--help"]
	```

	The most common indicator of whether this is appropriate is that the image `Dockerfile` starts with [`scratch`](https://registry.hub.docker.com/_/scratch/) (`FROM scratch`).


# CAP


PostgreSQL
sudo docker build --rm -t="lee5hxi/postgres" .

sudo docker run --name some-postgres -e POSTGRES_PASSWORD=postgres -d -p 5432 lee5hxi/postgres


CAP