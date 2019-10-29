# Web Quiz Engine
A simple engine for creating and solving quizzes through HTTP API.

It uses an embedded H2 database to store all data in the file system.

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

By default, it runs on the port `8888` and works with the `quizdb` database in the user's home directory.

## Description

At this stage, the service API supports creating, getting, and solving quizzes.
Each quiz has an id, title, text, some options. Some of the options are correct (from 0 to all).
The answer is not returned in the API.

## Operations and their results

To perform any actions with quizzes a user has to be registered and then authorized via HTTP Basic Auth.
Otherwise, the service returns the `HTTP 401 (Unauthorized)` code.

The following are examples of all supported requests and responses using `curl`.

---

### Register a new user

To register a new user, you need to send a JSON with `email` and `password` via `POST` request.
Here is an example:

```
curl -X POST -H "Content-Type: application/json" http://localhost:8888/api/register \
--data '{"email":"test@gmail.com", "password": "secret"}'
```

The service returns 200, if the registration has been completed successfully.

If the email is already taken by another user, the service will return `HTTP 400`.

Here are some additional restrictions to the format of user credentials:
- an email must have a valid format (with `@` and `.`);
- password must have at least five characters.

If any of them are not satisfied, the service will also return `HTTP 400`.

All the following operations needs a registered user to be successfully completed.

---

### Create a new quiz

To create a new quiz, you need to send a JSON via `POST` request with the following keys: 
- `title`: string, required;
- `text`: string, required;
- `options`: an array of strings, it's required, and should contain at least 2 items; 
- `answer`: an array of indexes of correct options, it's optional since all options can be wrong.

An example of the request:

```
curl --user test@gmail.com:secret -X POST -H "Content-Type: application/json" \
-d '{"title":"The Java Logo", "text":"What is depicted on the Java logo?", "options": ["Robot", "Tea leaf", "Cup of coffee", "Bug"], "answer": [2]}' \
http://localhost:8888/api/quizzes
```

The response contains the same JSON with generated `id`, but does not include `answer`.
```json
{"id":1,"title":"The Java Logo","text":"What is depicted on the Java logo?","options":["Robot","Tea leaf","Cup of coffee","Bug"]}
```

If the request JSON does not contain `title` or `text`, or they are empty strings (`""`), then the response is `404`.
If the number of options in the quiz is less than 2, the response is `404` as well.

---

### Get a quiz

To get an info about a quiz, you need to specify its `id` in url.

```
curl --user test@gmail.com:secret -X GET http://localhost:8888/api/quizzes/1
```

The response does not contain `answer`:
```json
{"id":1,"title":"The Java Logo","text":"What is depicted on the Java logo?","options":["Robot","Tea leaf","Cup of coffee","Bug"]}
```

If the quiz does not exist, the server returns `HTTP 404`.

---

### Get all quizzes (with paging)

The number of stored quizzes can be very large. 
In this regard, obtaining all quizzes is performed page by page: 10 quizzes at once.
Here is an example"

```
curl --user test@gmail.com:secret -X GET http://localhost:8888/api/quizzes
```

The response contains a JSON with quizzes (inside `content`) and some additional metadata:

```json
{
"totalPages":1, "totalElements":3, "last":true, "first":true, "sort":{ }, "number":0, 
"numberOfElements":3, "size":10, "empty":false, "pageable": { },
"content":[
  {"id":102,"title":"Test 1","text":"Text 1","options":["a","b","c"]},
  {"id":103,"title":"Test 2","text":"Text 2","options":["a", "b", "c", "d"]},
  {"id":202,"title":"The Java Logo","text":"What is depicted on the Java logo?","options":["Robot","Tea leaf","Cup of coffee","Bug"]}]
}
```

We can pass the `page` param to navigate through pages `/api/quizzes?page=1`. 
Pages start from 0 (the first page).

If there is no quizzes, `content` is empty.

In all cases, the status code is `HTTP 200 (OK)`.

---

### Solving a quiz

To solve a quiz, you need to pass an answer JSON-array with option indexes via `POST` request.

Here is an example with `curl`:
```
curl --user test@gmail.com:secret -X POST -H 'Content-Type: application/json' \
http://localhost:8888/api/quizzes/1/solve --data '[1, 2]'
```

It is also possible to send an empty array of options because some quizzes may not have correct options.
```
curl --user test@gmail.com:secret -X POST -H 'Content-Type: application/json' \
http://localhost:8888/api/quizzes/1/solve --data '[]'
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

---

### Get all completions of quizzes (with paging)

The API provides an operation to get all completions of quizzes for a user.
A response is separated by pages, since the service may return a lot of data.

```
curl --user test@gmail.com:secret -X GET  http://localhost:8888/api/quizzes/completed
```

The response contains a JSON with quizzes (inside `content`) and some additional metadata:

```json
{"content":[
  {"quizId":103,"quizTitle":"Test 3","completedAt":"2019-10-29T21:13:53.779542"},
  {"quizId":102,"quizTitle":"Test 2","completedAt":"2019-10-29T21:13:52.324993"},
  {"quizId":101,"quizTitle":"Test 1","completedAt":"2019-10-29T18:59:58.387267"},
  {"quizId":101,"quizTitle":"Test 1","completedAt":"2019-10-29T18:59:55.303268"},
  {"quizId":202,"quizTitle":"The Java Logo","completedAt":"2019-10-29T18:59:54.033801"}],
  "totalPages":1,"totalElements":5,"last":true,"first":true, "empty":false}
```

Since it is allowed to solve a quiz multiple times, the response may contain duplicate quizzes, 
but with different completion date.

We removed some metadata keys from the response to keep it more simple to understand.

---

### Deleting a quiz

It is possible to delete a quiz, but this can only be done by its creator.

```
curl --user test@gmail.com:secret -X DELETE  http://localhost:8888/api/quizzes/1
```

If the operation was successful, the service returns `HTTP 204 (No content)`.

If the specified quiz does not exist, the server returns `HTTP 404`.
If the specified user is not the creator of this quiz, the response contains `HTTP 403 (Forbidden)`.
