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

At this stage, the service API supports creating, getting, and solving quizzes.
Each quiz has an id, title, text, some options. Some of the options are correct (from 0 to all).
The answer is not returned in the API.

## Operations and their results

The following are examples of all supported requests and responses using `curl`.

### Create a new quiz

To create a new quiz, you need to send a JSON via `POST` request with the following keys: 
- `title`: string, required;
- `text`: string, required;
- `options`: an array of strings, it's required, and should contain at least 2 items; 
- `answer`: an array of indexes of correct options, it's optional since all options can be wrong.

An example of the request:

```
curl -X POST -H "Content-Type: application/json" -d '{"title":"The Java Logo", "text":"What is depicted on the Java logo?", "options": ["Robot", "Tea leaf", "Cup of coffee", "Bug"], "answer": 2}' http://localhost:8888/api/quizzes
```

The response contains the same JSON with generated `id`.
```json
{"id":1,"title":"The Java Logo","text":"What is depicted on the Java logo?","options":["Robot","Tea leaf","Cup of coffee","Bug"]}
```
It does not include `answer`.

If the request JSON does not contain `title` or `text`, or they are empty strings (`""`), then the response is `404`.
If the number of options in the quiz is less than 2, the response is `404` as well.

### Get a quiz

To get an info about a quiz, you need to specify its `id` in url.

```
curl -v -X GET http://localhost:8888/api/quizzes/1
```

The response does not contain `answer`:
```json
{"id":1,"title":"The Java Logo","text":"What is depicted on the Java logo?","options":["Robot","Tea leaf","Cup of coffee","Bug"]}
```

If the quiz does not exist, the server returns `HTTP 404`.

### Get all quizzes

To get all existing quizzes, you need to make a request without any params:

```
curl -v -X GET http://localhost:8888/api/quizzes
```

The response contains a JSON array of quizzes:

```json
[{"id":1,"title":"The Java Logo","text":"What is depicted on the Java logo?","options":["Robot","Tea leaf","Cup of coffee","Bug"]},
{"id":2,"title":"The Ultimate Question","text":"What is the answer to the Ultimate Question of Life, the Universe and Everything?","options":["Everything goes right","42","2+2=4","11011100"]}]
```

If there is no quizzes, it returns an empty JSON array:

```json
[]
```

In both cases, the status code is 200 (OK).

### Solving a quiz

To solve a quiz, you need to pass an answer JSON-array with option indexes via `POST` request.

Here is an example with `curl`:
```
curl -X POST -H 'Content-Type: application/json' http://localhost:8888/api/quizzes/1/solve --data '[1, 2]'
```

It is also possible to send an empty array of options because some quizzes may not have correct options.
```
curl -X POST -H 'Content-Type: application/json' http://localhost:8888/api/quizzes/1/solve --data '[]'
```

The result is determined by the value of the boolean `success` key in the response json.

- if the answer is correct:
```json
{"success":true,"feedback":"Congratulations, you're right!"}
```

- if the answer is incorrect:
```json
{"success":false,"feedback":"Wrong answer! Please, try again."}
```

- If the specified quiz does not exist, the server returns `HTTP 404`.


