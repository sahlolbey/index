# Solative Index Calculation Challenge (v1.03)

### Build the project by maven
It is assumed that the maven and jdk8+ is already installed on your system and configured to be in 
path. 

    mvn clean package

### Run the project 

    java -jar target\index-0.0.1.jar

### My Assumptions

* It is assumed that we don't need the prices after 60 seconds so I removed them from memory and no longer keep 
them any where.

* I assumed that there is only a single service responding to requests and there is no load balancing.

### If I had more time
I think in real world applications it is required to store prices during time for future uses, so If I 
had more time I would store the old prices in a database for future uses. 

