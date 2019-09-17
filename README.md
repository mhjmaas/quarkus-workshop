# Quarkus workshop

Welcome to the quarkus workshop. The end result of this lab will be an indecision app which will decide for you out of a list of todo's. We will aim to build the todos api using Quarkus and store the data in a MongoDB instance.

Before we begin we need to have a number of things installed, lets make sure you have the following prerequisites.

## Prerequisites 
- Java IDE, whichever one you like
- JDK 8 or 11
- Apache Maven 3.5.3+
- A github account (easiest way for deploying our apps on openshift)

## Lab 1: Introduction

Since Quarkus has some excellent examples to get you started we will first create the "Greeting" application and deploy it to openshift. This guide will provide you with a first insight into how easy is to get started with quarkus using coding concepts you already know.

1. Go to the [Getting started guide](https://quarkus.io/guides/getting-started-guide) and complete this tutorial. When you reach step 10, please return here.
2. You have been running quarkus using the JAVA VM in this guide. However one of the biggest wins of Quarkus (Well, GraalVM actually, but who's counting really?) is the ability to compile to a native images. You can do this command-line but it would be much cooler to let Openshift do this for you. So without further ado, lets build and run your brand spanking new super useful greeting application in Openshift.
3. Create a github project for your greeting app and commit and push the greeting application into it. (eg: [quarkus-getting-started.git](https://github.com/mhjmaas/quarkus-getting-started.git))
4. Get the login for the openshift console from the organizers together with a login. Now login to the openshift cluster (use the htpasswd option) and download the OC command line tool. (Click on the question mark in the top right, then click on "command-line tools" and download OC for your os. Make sure to add OC to your PATH, or simply run commands from the directory you have downloaded the OC executable.
5. Create a new openshift project. Give it a name like quarkus-<your-name>
6. Login with the OC tool by going to the console, clicking the question mark in the top right again and choosing "Copy login command". You now need to login again and you will be presented with a token you can copy and paste into your terminal to login with OC. 
7. Try "oc project" to get your created project. If not, type: "oc project quarkus-<your-name>" to select it.
8. Now lets go and run the one-liner command to clone your greeting app from your github account and build it into a native application and run it on openshift. Go to the terminal you have used to login to OC and run the following command:
`oc new-app quay.io/quarkus/ubi-quarkus-native-s2i:19.1.1~<your-http-git-repo>  --name=greeting`
9. Have a look at the web console. Go to the menu -> Builds -> Builds. You should see a greeting-1 build. Have a look at the resources and the log. Have some coffee while this build runs. It takes quite a few minutes and as you might notice, it uses quite significant resources like CPU and Memory. (When you use minishift on your private laptop, you might need to increase cpu and memory limits to 4 cores and 4gb) When the build is done you will notice an image is being pushed to the image registry.
10. Have a look at the project overview in the web console. When the build is done you should see a pod spinning up. (1 of 1 pods available)
11. We can now test our greeting application. We probably first need to configure a "route" to get outside HTTP access to the greeting resource. Go to menu -> networking -> routes and click on "create route". Name your route "greeting and select the greeting service from the dropdown. Click on "Create". You can select the created route to display the url. Go to "<your-greeting-route>/hello/greeting/your-name". You should now be greeted by a native quarkus application. Alternatively run the oc command: `oc expose svc/greeting`
12. Have a look at the pod for the "greeting" app. It uses very little memory.

## Lab 2: Creating the Todo REST service

In this lab we will set up our Todo application which will serve as an endpoint for a web client. This application needs to be able to list a number of options as well as add and remove options. Finally it needs a way to clear all options.

1. We will use a maven command to scaffold our quarkus application with all of its needed dependencies. Run in terminal: 

`mvn io.quarkus:quarkus-maven-plugin:0.21.2:create -DprojectGroupId=nl.terra10 -DprojectArtifactId=TodosApi -DclassName="nl.terra10.api.TodosResource" -Dpath="/todos" -Dextensions="resteasy-jsonb"`

2. Now try and run the application using `./mvnw compile quarkus:dev` It should run without errors
3. Add the provided CorsFilter.java file, (in the root of this repo) and copy it into the nl.terra10.api directory. This allows request from everywhere. (I have found the declarative Quarkus cors settings do not work really well)
4. Now comes your assignment. Create the application by yourself. You need to do this by following these steps:
   - Create a model file: Todo.java with 2 properties: "id" and "option". No magic here, its a POJO with the following requirements:
     - Includes a no-arg constructor
     - Includes a constructor for both id and option
     - Has getters and setters for both id and option
     - Has an overridden "public boolean equals(Object obj)" method as well as an overridden "public int hashCode()" method. (tip: use Objects.equals and Objects.hash)
   - Create a service file: TodoService.java which will have add, remove, list and removeAll operations using the Todo model. Also, just save the data in a List<Todo> in the service for now. (Which is a dumb thing to do, I know) Use @ApplicationScoped on the class to make it injectable.
   - Modify the TodosResource to inject the TodoService (tip: @Inject) and add rest operations using the following paths:
     - GET / -> List all todos (tip: @GET)
     - POST / -> Add the provided todo (tip: @POST)
     - DELETE /{id} -> Remove the todo with the specified id. (tip: @DELETE @PATH("{id}") -> with @PathParam("id"")
     - DELETE / -> remove all todos  (tip: @DELETE)
5. You should be able to test your application by using postman or curl or whatever to fire of requests to your http://localhost:8080/todos/ endpoint
6. Remove the test files for now, this workshop is not about unit testing, however important it may be. :)
7. When you have tested your application locally, then create a new github repo for your application and commit and push it to this repo.
8. Use the skills learned in Lab 1 (from point 8) to create a native deployment for your todos api in openshift. Name it "todosapi"
9. Test and see if your application works in openshift by using postman. (Do not forget to create a route or you will not be able to connect)

## Lab 3: Deploying a sexy ui to make use of your api.

In this lab we will modify a pre-created react app to use our Todo API as a backend. Disclaimer: This UI is in no way production ready and the way we connect it to your api is also not recommended. 

1. Fork and clone the "https://github.com/mhjmaas/react-indecisionapp" on your machine.
2. Modify the api endpoint property in the "src/components/IndecisionApp.js" file, and commit and push this code to your repo. (In real life we would use an environment variable here, to make the endpoint configurable without having to modify code)
3. Navigate to the openshift web console, and go to Catalog -> Developer Catalog. Select the "Tech Preview - Modern Web Applications" app template.
4. Click on "Create Application", make sure your namespace is selected and give the name "indecision". Provide the git url of the application you forked. Also select: "Create route" and click on create.
5. Your application is now being cloned, built and deployed. When the build and deployment are finished, navigate to the created route and test if your application works.
   You can also find the route url by clicking on the "indecision" app in the project status overview. The pane on the right side displays the route at the bottom.
6. Bask in the glory of never having to make a decision yourself again.


## Lab 4: Connect to a mongodb instance

In this lab we will no longer store information in memory, but use a mongodb database to persist the data.

1. Return to the TodosApi we created in Lab 2. We need to add an extension to include mongodb support go to the directory of the "TodosApi" and type the following command: `mvn quarkus:add-extension -Dextensions="io.quarkus:quarkus-mongodb-client"`
2. Modify the TodoService.java file and inject the mongo client:
   ```
   @Inject
   MongoClient mongoClient;
   ```
3. Add a getCollection() method:
   ```
   private MongoCollection getCollection() {
       return mongoClient.getDatabase("todos").getCollection("todos");
   }
   ```
4. Modify the list method as follows:
   ```
   public List<Todo> list() {
       List<Todo> list = new ArrayList<>();
       MongoCursor<Document> cursor = getCollection().find().iterator();

       try {
           while (cursor.hasNext()) {
               Document document = cursor.next();
               Todo todo = new Todo();
               todo.setId(document.getInteger("id"));
               todo.setOption(document.getString("option"));
               list.add(todo);
           }
       } finally {
           cursor.close();
       }
       return list;
   }
   ```
 5. Modify the add, remove and removeAll commands to use the getCollection() method and add or remove data from the database.
    (tip: create a new Document and use the findOneAndDelete and deleteMany methods. Have a google. :)
 6. When done, add the following key-value pair to the application.properties file:
    `quarkus.mongodb.connection-string = mongodb://localhost:27017`
 7. If you have docker installed and running, run the following command to start a local mongodb instance.
    `docker run -ti --rm -p 27017:27017 mongo:4.0`
 8. Run the quarkus application locally and test using your favorite rest client.
 9. If everything works, commit and push your code to github.
 10. Now before we can rebuild our application in Openshift, we have to set up a mongodb instance on openshift. 
     Do so by using the following command:
     `oc new-app --image-stream=mongodb -e MONGODB_USER=todosuser -e MONGODB_PASSWORD=password -e MONGODB_DATABASE=todos -e MONGODB_ADMIN_PASSWORD=password`
     You can have a look at the web console and see your mongodb instance getting pulled and started in your project
 11. To override our default mongodb connection string we need to go to Openshift web console, and navigate to "workloads -> Deployment Configs".
     select our "todosapi" and go to environment. Add another key-value pair here which will be added to the environment variables for the running pods:
     QUARKUS_MONGODB_CONNECTION_STRING with value: "mongodb://todosuser:password@mongodb:27017/todos". Click "save". This will trigger a redeployment,
     but since we have not rebuild our image nothing of notice will happen.
 12. Rebuild our image based of the new sources by going to: "Builds -> Build Configs -> todosapi". Click on "Actions" in the top right corner and select "Start build".
     You will be taken to the builds screen, and when it is finished it will automatically redeploy the image. When this is done, try the indecision app again. It should now read and save data from this mongodb instance
     
 This concludes the labs, if you have time left, you can perhaps try to add some unit tests to the todosapi or build in functionality for deciding a todo as well. It is up to you! I hope you enjoyed these labs. You are free to use these labs and share them, as long as you give credit to Terra10.
