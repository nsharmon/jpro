Welcome to jPro
====
jPro is a purely Java implementation of Prolog language.  Why would you want to have a *Java*-based implementation of Prolog?  What's wrong with Prolog?  Nothing is wrong with Prolog.  I wanted to provide a library by which Prolog can be used without requiring installations of a Prolog compiler.  Is it the kind of library you'd likely use everyday?  Yes!  Well, okay, no probably not.  Prolog helps you to reason through your logic, and either find the logical conclusion given the facts you enter, or at least helping you to realize mistakes in said facts.

The project can be loaded using Eclipse with a maven plugin.  You can find the source in the src folder and testing code in test folder.  I recommend that after having downloaded the project, you run some of the test suites to verify that everything is up and running.  I wish to make the library easily compiled and deployable, though for now it is merely an eclipse project.  

The source structure is as follows:

com.nsharmon.jpro
	- tokenizer
		- listeners
	- parser
		- listeners
	- engine
		- statements
		
Text is loaded into the tokenizer character by character, and the listeners determine whether or not it recognizes that particular character as something it can consume.  If so, it consumes as much of the stream as possible and returns a token.  The tokenizer returns these tokens as an Iterator and they get passed along to the parser.  The parser works similarly in that it gets fed input and the listeners determine whether or not it can consume it, though rather than characters, it consumes tokens.  The parser then returns statements defined under com.nsharmon.jpro.engine.statements that make up the actual program.  The PrologProgram is responsible for executing each statement in the order in which they were read.  The output is printed to System.out or whatever PrintStream that gets passed to PrologProgram, however only QueryStatements return results that are collected after each run.  

There is still much *much* left to do.  It can do basic fact checking, but not much else.  While I do appreciate any feedback, please keep in mind that it is a work in progress.  If something is not clear, I will be happy to point you in the right direction.  
