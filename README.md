# exchangerate

Input should be given via .txt files. Sample file in int batchfile/operations.txt

1) run the code with ./gradlew clean build in mac 
    or in windows gradlew clean build
2) Install docker and run the docker build command : go the the project folder and run docker command as shown
   docker build -t authorizerapp .
3) To run the application use following command :
   docker run -d -p 8080:8080 authorizerapp
4) command to view docker process : docker ps   
5) docker logs "container id" : will give the logs and can view the transactions
   status
6) TO shut down the docker use command : docker shutdown name

Another way to run the application is :

java -jar C:\Users\udayr\IdeaProjects\authorizerapp\build\libs\authorizerapp-0.0.1-SNAPSHOT.jar C:\batchfiles\operations.txt