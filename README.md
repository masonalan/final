# FINAL PROJECT 2

The code for the client is in `SearchEngine` and the code for the server is in `SearchBackend`. The server code is already running on a Hadoop cluster in Google Cloud. However, you can build the `SearchEngine` code from the source using Docker.

### Notes & Assumptions
1. In the inverted indices mapper splits each line up by spaces. The drawbacks to this approach is that "end." and "end" would be considered as two different words in the final inverted indices table. This only effects the "Top-N" count, as my implementation of the "Term" search checks to see if an index contains a search query. I found this drawback to not matter much, because the highest ranked words are all conjunctions, and by nature, would never end a sentence or be possesive.
2. For "Term" search, I checked to see if an index contained the term. This method returned the same counts of "king" in "kinghenryiv" and "kinghenryvi" that were in the mockups. This also most closely resembles "cmd-f" which also resulted in the same counts of "king" in those files.
3. The inverted indices mapper also checks to make sure that an index has at least one letter in it (originally, when I ran the "Top-N" count, " " was the most commonly used word. 
4. I decided to show the entire path of each file rather than it's parent directory (this is contrary to the mockup).

### Build & Run
1. Move "Credentials.json" to inside `/SearchEngine` and inside `/SearchEngine/src/main/resources/`
2. Make sure X11 is installed.
3. `cd` into `SearchEngine` in a new Terminal.
4. Run `docker build --tag v1 .`
5. Run `docker run -v /tmp/.X11-unix:/tmp/.X11-unix --rm -it -e DISPLAY=$(ipconfig getifaddr en0):0 v1:latest`. If `ifconfig` is unavailable on your machine, replace `$(...)` with your local IP address with respect to your router.
