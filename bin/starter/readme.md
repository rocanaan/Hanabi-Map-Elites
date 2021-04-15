# Fireworks sample controllers

## This contains a few things
* A runner so you can test your agent
* A sample agent
* A sample rule based agent

## Submitting your entry
* You should rename the package to your username on the submission server
* You should change the agent class name to your username

## Technical Details
Build script overrides:

```
# TODO check the package is named correctly
mvn -o -B -Djar.finalName=$SUBMIT_USERNAME clean package
```

