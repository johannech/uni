from flask import Flask
from flask import render_template
from flask import request
from feedline import feedline

retValue = feedline('')
APP = Flask(__name__)

@APP.route('/')
def index():
	return render_template('prompt.html')

@APP.route('/prompt', methods=['POST'])
def prompt():
	global retValue
	command = request.form['command']
	retValue += feedline(command)
	#retValue = retValue.replace("\n", "<br />")

	return render_template('prompt.html',
							input=retValue)

if __name__ == "__main__":
	APP.run(debug=True)