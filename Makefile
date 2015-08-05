all: compile
	@echo -e '[INFO] Done!'
clean:
	@echo -e '[INFO] Cleaning Up..'
	@-rm -rf cs455/**/*.class

compile: 
	@echo -e '[INFO] Compiling the Source..'
	@javac -d . -cp ./lib/jericho-html-3.3.jar:. cs455/**/*.java
