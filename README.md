# Web Quiz Engine
A simple engine for creating and solving quizzes through HTTP API.

## Operations and their results

The following are examples of all supported requests and responses using `curl`.

### Create a new quiz

To create a new quiz, you need to send a json with the four keys: `title`, `text`, `options` (array of strings) and `answer`. 
At this moment, all these keys are optional.

An example:

```
curl -X POST -H "Content-Type: application/json" -d '{"title":"The Java Logo", "text":"What is depicted on the Java logo?", "options": ["Robot", "Tea leaf", "Cup of coffee", "Bug"], "answer": 2}' http://localhost:8888/api/quizzes
```

The result contains the same json with generated `id`.
```
{"id":1,"title":"The Java Logo","text":"What is depicted on the Java logo?","options":["Robot","Tea leaf","Cup of coffee","Bug"]}
```
It does not include `answer`.

### Get a quiz

To get an info about a quiz, you need to specify its `id` in url.

```
curl -v -X GET http://localhost:8888/api/quizzes/1
```

The result does not contain `answer`:
```
{"id":1,"title":"The Java Logo","text":"What is depicted on the Java logo?","options":["Robot","Tea leaf","Cup of coffee","Bug"]}
```

If the quiz does not exist, the server returns `HTTP 404`.