# Python Code Evaluator
This Spring Boot application is designed to evaluate Python code using various tests such as Pylint, Pytest,PiPreQs and Duplication Code Detection Tool. With this application, you can test your Python code and receive feedback on its quality and performance.

# Requirements
## Pylint 

Pylint is a [static code analyser](https://en.wikipedia.org/wiki/Static_code_analysis) for Python 2 or 3. The latest version supports Python 3.7.2 and above.

Pylint analyses your code without actually running it. It checks for errors, enforces a coding standard, looks for [code smells](https://martinfowler.com/bliki/CodeSmell.html), and can make suggestions about how the code could be refactored. Pylint can infer actual values from your code using its internal code representation (astroid).

For command line use, pylint is installed with:

`pip install pylint`

It can also be integrated in most editors or IDEs. More information can be found [in the documentation](https://pylint.pycqa.org/en/latest/user_guide/installation/index.html).

More info here: https://pylint.pycqa.org/en/latest/

## Pytest

This plugin produces coverage reports. Compared to just using coverage run this plugin does some extras:

Subprocess support: you can fork or run stuff in a subprocess and will get covered without any fuss.
Xdist support: you can use all of pytest-xdist’s features and still get coverage.
Consistent pytest behavior. If you run `coverage run -m pytest` you will have slightly different sys.path (CWD will be in it, unlike when running pytest).

Install with pip:

`pip install pytest-cov`
For distributed testing support install pytest-xdist:

`pip install pytest-xdist`

More info here: https://pytest-cov.readthedocs.io/en/latest/readme.html

## Duplicate Code Detection Tool

A simple Python3 tool (also available as a [GitHub Action](https://github.com/platisd/duplicate-code-detection-tool#github-action)) to detect similarities between files within a repository.

The following Python packages have to be installed:

nltk
`pip3 install --user nltk`

gensim
`pip3 install --user gensim`

astor
`pip3 install --user astor`

punkt
`python3 -m nltk.downloader punkt`

More info for how to use it here: https://github.com/platisd/duplicate-code-detection-tool#example

## Pipreqs
Generate requirements.txt file for any project based on imports

Installation
`pip install pipreqs`

# Installation
+ Clone this repository.
+ Make sure you have Java and Python installed on your system.
+ Navigate to the root directory of the application.
+ Build the project with `mvn clean install`.
+ Run the application with `mvn spring-boot:run`.

# Usage
Make a POST request to /projects/{git_url}

The application will evaluate the code using Pylint, Pytest,PiPreQs and Duplication Code Detection Tool.

The evaluation results will be returned as a JSON object.

# Contributing
Contributions to this project are welcome! If you find a bug or would like to suggest a new feature, please open an issue on GitHub. If you would like to contribute code, please fork the repository and submit a pull request.

# License
This project is licensed under the Eclipse Public License - v 2.0. See LICENSE.md for details.

## Thesis

This project is a part of my thesis on evaluating Python code using Pylint, Pytest, and Duplication Code Detection Tool. The goal of this Spring Boot application is to provide users with data about their Python code through various tests, including Pylint, Pytest, and Duplication Code Detection Tool. With this application, users can easily evaluate their code and receive feedback on potential errors, performance issues, and duplicated code.

### Thesis Title
"EVALUATION OF PYTHON CODE QUALITY USING MULTI-METRIC ANALYSIS"

### University
[University of Macedonia](www.uom.gr)

## Supervisor
[Dr. Alexander Chatzigeorgiou](https://gr.linkedin.com/in/alexanderchatzigeorgiou)
