from sys import stdout, stdin
from getchar import getchar
from feedline import feedline
from inspect import getdoc

"""Small method for setting the pointer hist_pos.

Args:
	arrow_dir(string): either '[A' up-arrow or '[B' down-arrow
Returns:
	the correct value when the up/down-arrow is pressed.
Raises:
	Nothing
Example usage:
	>>> hist_pos = setHistPos(arrow_dir, hist_pos, history)

"""

def setHistPos(arrow_dir, hist_pos, history):
	if arrow_dir == '[A': hist_pos -= 1
	elif arrow_dir == '[B': hist_pos += 1

	if hist_pos >= 0:
		return -1

	if hist_pos < -len(history):
		return -len(history)
	return hist_pos


"""Method for prompt.

Args:
	Nothing
Returns:
	Nothing
Raises:
	Nothing

	Does something with:
		\x1b - Arrow keys is pressed, check history and return element to the user
		\x04 - ctrl-d is pressed. Pressed twice or once without something on the line exits the program
		\r or \n - returns and puts the command into history
Example usage:
	Welcome to mypython!
	in[0]: print 'hei'
	hei
	in[1]: 1			Adding elements to history
	out[1]: 1
	in[2]: 2
	out[2]: 2
	in[3]: 3
	out[3]: 3
	in[4]: 4
	out[4]: 4
	in[5]: 
	in[5]: 4			--------
	in[5]: 3
	in[5]: 2
	in[5]: 1
	in[5]: 2			Up and down arrow
	in[5]: 3
	in[5]: 4
	in[5]: 4
	out[5]: 4			--------
	in[6]: 
	kthanksbye!
"""
def prompt():
	history = []
	print "Welcome to mypython!"
	buffr = ""
	hist_pos = 0
	stdout.write(feedline('')) # to write the first line

	while True:
		usr_input = getchar()

		if usr_input == '\x1b':
			if history != []:
				arrow_dir = stdin.read(2)
				if arrow_dir == '[C' or arrow_dir == '[D':
					continue
				else:
					stdout.write('\n' + feedline('\n'))
					hist_pos = setHistPos(arrow_dir, hist_pos, history)
					buffr = history[hist_pos]
					stdout.write(buffr)
			continue
					

		stdout.write(usr_input)
		buffr += usr_input # add to buffer if not arrows
		
		if usr_input == '\x04':
			if buffr == '\x04':
				stdout.write('\nkthanksbye!\n')
				break

			else:
				buffr = ''
				stdout.write('\nKeyboardinterupt')
				stdout.write('\n' + feedline(buffr))
				continue

		if usr_input == '\r' or usr_input == '\n':
			
			stdout.write('\n' + feedline(buffr))
			
			if buffr.startswith('%save'):
				buffr = buffr.split(' ', 1)[1]
				text_file = open(buffr + '.txt', 'w')
				text_file.write('History:\n')
				for element in history:
					text_file.write(element + '\n')
				text_file.close()

			if buffr.strip() != '':
				history.append(buffr)
			buffr = ''



if __name__ == "__main__":
	prompt()