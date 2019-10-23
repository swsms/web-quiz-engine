# Web Quiz Engine
A simple engine for creating and passing quizzes through HTTP API.

## Operations and their results

Create a new quiz example:

```
curl -X POST -H "Content-Type: application/json" -d '{"title":"The Java Logo", "text":"What is depicted on the Java logo?", "options": ["Robot", "Tea leaf", "Cup of coffee", "Bug"], "answer": 2}' http://localhost:8888/api/quizzes
```

The result will content the "id" attribute:
```
{"id":1,"title":"The Java Logo","text":"What is depicted on the Java logo?","options":["Robot","Tea leaf","Cup of coffee","Bug"],"answer":2}
```