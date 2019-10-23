# Web Quiz Engine
A simple engine for creating and solving quizzes through HTTP API.

## Running the application

- Building app
```
./gradlew build
```

- Running app
```
java -jar build/libs/*.jar
```

- Or simply
```
./gradlew bootRun
```

## Description

At this stage, the service API always return the same quiz to be solved. 
The quiz has a title, text and four options. The correct answer is third.

## Operations and their results

The following are examples of all supported requests and responses using `curl`.

### Get the quiz

Here is an example how to request the hardcoded quiz.
```
curl -v -X GET http://localhost:8888/api/quizzes/1
```

The response looks like the following:
```
{"title":"The Java Logo","text":"What is depicted on the Java logo?","options":["Robot","Tea leaf","Cup of coffee","Bug"]}
```
It does not contain the answer.

### Solving a quiz

To solve a quiz, you need to pass the `answer` param using the `POST` request.
The parameter means the index of the answer (it starts with 0).
The result is determined by the value of the boolean `success` key in the response json.

Here is an example with `curl`:
```
curl -X POST http://localhost:8888/api/quizzes?answer=2
```

- if the answer is correct:
```
{"success":true,"feedback":"Congratulations, you're right!"}
```

- if the answer is incorrect:
```
{"success":false,"feedback":"Wrong answer! Please, try again."}
```
