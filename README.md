# FINAL PROJECT 2

1. Move "Credentials.json" to inside `/SearchEngine` and inside `/SearchEngine/src/main/resources/`
2. Make sure X11 is installed.
3. `cd` into `SearchEngine` in a new Terminal.
4. Run `docker build --tag v1 .`
5. Run `docker run -v /tmp/.X11-unix:/tmp/.X11-unix --rm -it -e DISPLAY=$(ipconfig getifaddr en0):0 v1:latest`. If `ifconfig` is unavailable on your machine, replace `$(...)` with your local IP address with respect to your router.
