# Assignment 1

Luke Newman 
ID: 18327357

# Build the project

Navigate to the 'work' folder and build the project using the command:

mvn package

# Create the index and select the desired scoring method

Choose a scoring method: vsm / bm25 / boolean, then execute the project using the command:

java -jar target/assignment-1-1.0-SNAPSHOT.jar

Followed by the scoring method, for example:

java -jar target/assignment-1-1.0-SNAPSHOT.jar vsm

Results will then be stored in the QueryResults folder

# Trec Eval

Then for running trec_eval to get scoring results, go back to the work folder and clone the repository using:

git clone https://github.com/usnistgov/trec_eval.git

Then navigate to the trec_eval folder and run the command

make

Then run trec_eval as well as the relevant docs with the results file, in this case:

./trec_eval ../corpus/QRelsCorrectedforTRECeval ../QueryResults/Scores.vsm