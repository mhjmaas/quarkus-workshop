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

1. Go to the [Getting started guide](https://quarkus.io/guides/getting-started-guide) and complete this tutorial. When you reach step 11, please return here.
2. You have been running quarkus using the JAVA VM in this guide. However one of the biggest wins of Quarkus (Well, GraalVM actually, but who's counting really?) is the ability to compile to a native images. You can do this command line but it would be much cooler to let Openshift do this for you. So without further ado, lets build and run your brand spanking new super useful greeting application in Openshift.
3. Create a github project for your greeting app and commit and push the greeting application into it.
4. Create a new openshift project. Give it a name like quarkus-<your-name>
5. Get the login for the openshift console from the organizers together with a login. Now login to the openshift cluster and download the OC command line tool. (Click on the question mark in the top right, then click on "command-line tools" and download OC for your os. Make sure to add OC to your PATH, or simply run commands from the directory you have downloaded the OC executable.
6. Login with the OC tool by going to the console, clicking the question mark in the top right again and choosing "Copy login command". You now need to login again and you will be presented with a token you can copy and paste into your terminal to login with OC. 
7. Try "oc get project" to get your created project. If not, type: "oc project quarkus-<your-name>" to select it.
8. Now lets go and run the one-liner command to clone your greeting app from your github account and build it into a native application and run it on openshift. Go to the terminal you have used to login to OC and run the following command:
`oc new-app quay.io/quarkus/ubi-quarkus-native-s2i:19.1.1~<your-git-repo>  --name=greeting -e QUARKUS_OPTS=-Xmx24M -Xms8m`
9. Have a look at the web console. Go to the menu -> Builds -> Builds. You should see a greeting-1 build. Have a look at the resources and the log. Have some coffee while this build runs. It takes quite a few minutes and as you might notice, it uses quite significant resources like CPU and Memory. (When you use minishift on your private laptop, you might need to increase cpu and memory limits to 4 cores and 4gb) When the build is done you will notice an image is being pushed to the image registry.
10. Have a look at the project overview in the web console. When the build is done you should see a pod spinning up. (1 of 1 pods available)
11. We can now test our greeting application. We probably first need to configure a "route" to get outside HTTP access to the greeting resource. Go to menu -> networking -> routes and click on "create route". Name your route "greeting and select the greeting service from the dropdown. Click on "Create". You can select the created route to display the url. Go to "<your-greeting-route>/hello/greeting/your-name". You should now be greeted by a native quarkus application.
12. Have a look at the pod for the "greeting" app. It uses very little memory.

## Lab 2: Creating the Todo REST service

In this lab we will set up our Todo application which will serve as an endpoint for a web client. This application needs to be able to list a number of options as well as add and remove options. Finally it needs a way to clear all options.

1. We will use a maven command to scaffold our quarkus application with all of its needed dependencies. (You will see a mongo reference, which is necessary for the next lab). Run in terminal: 

`mvn io.quarkus:quarkus-maven-plugin:0.21.2:create -DprojectGroupId=nl.terra10 -DprojectArtifactId=TodosApi -DclassName="nl.terra10.api.TodosResource" -Dpath="/todos" -Dextensions="resteasy-jsonb,mongodb-client"`

2. Now try and run the application using `./mvnw compile quarkus:dev` It should run without errors
3. Add the provided CORSFilter. This allows request from everywhere. (I have found the declarative Quarkus cors settings do not work really well)
4. Now comes your assignment. Create the application by yourself. You need to do this by following these steps:
   - Create a model file: Todo.java with 2 properties: "id" and "option".
   - Create a service file: TodoService.java which will have add, remove, list and removeAll operations using the Todo model. Also, just save the data in a List<Todo> in the service for now. (Which is a dumb thing to do, I know)
   - Modify the TodosResource to inject the TodoService and add rest operations using the following paths:
     - GET / -> List all todos
     - POST / -> Add the provided todo
     - DELETE /{id} -> Remove the todo with the specified id.
     - DELETE / -> remove all todos 
5. You should be able to test your application by using postman or curl or whatever to fire of requests to your http://localhost:8080/todos/ endpoint
6. When you have tested your application locally, then create a new github repo for your application and commit and push it to this repo.
7. Use the skills learned in Lab 1 (from point 8) to create a native deployment for your todos api in openshift.
8. Test and see if your application works in openshift by using postman. (Do not forget to create a route or you will not be able to connect)

## Lab 3: Deploying a sexy ui to make use of your api.

In this lab we will modify a pre-created react app to use our Todo API as a backend. Disclaimer: This UI is in no way production ready and the way we connect it to your api is also not recommended. 

1. Fork and clone the "https://github.com/mhjmaas/react-indecisionapp" on your machine.
2. Modify the api endpoint property in the "src/components/IndecisionApp.js" file, and commit and push this code to your repo. (In real life we would use an environment variable here, to make the endpoint configurable without having to modify code)
3. 
