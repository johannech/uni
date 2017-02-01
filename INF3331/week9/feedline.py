import sys
import traceback
from StringIO import StringIO
from subprocess import Popen, PIPE
namespace = vars().copy()
counter = 0
# from feedline import feedline

"""Small method for setting the pointer hist_pos.

Args:
	command
Returns:
	An evaluation, execution of command. Also uses command for saving to file,
	using the help()-method or os command it
Raises:
	Object not %s found!
	traceback.format_exc().split('\n')[4]
Example usage:
	>>> feedline(%save hello)
	>>> feedline(object!)

"""

def feedline(command):
	global counter
	retValue = ""

	if command == '\n' or command == '\r' or command == '':
		return "in[%i]: " % (counter) 

	if command.strip().endswith('?'):
		command = command[:-2]
		try:
			retValue = getdoc(eval(command))
		except:
			retValue = "Object %s not found!" % command #ipython error

	elif command[0] == '!':
		command = command[1:]
		proc = Popen(command, shell=True, stdout=PIPE, stderr=PIPE)
		retValue = ''.join(proc.communicate()[:2])

	else:
		try:
			tmp = str(eval(command, namespace))
			retValue = "out[%i]: %s" % (counter, tmp)
		except:
			if(command.partition(' ')[0] == 'print'):
				oldio, sys.stdout = sys.stdout, StringIO()
				try:
					exec(command, namespace)
					retValue = sys.stdout.getvalue()

				except:
					retValue = traceback.format_exc().split('\n')[4]
				# Reset stdout
				sys.stdout = oldio
			else:
				try:
					exec(command, namespace)
					# Print out captured stdout
				except:
					retValue = traceback.format_exc().split('\n')[4]

		if retValue == '':
			return "in[%i]: " % (counter)
		counter+=1

	return "%s\nin[%i]: " % (retValue.strip('\n'), counter)
