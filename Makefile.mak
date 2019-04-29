JCC = javac
current_dir = $(shell pwd)
JFLAGS = -g

all:
	javac MasterBot.java
	javac SlaveBot.java

run_master:
	java MasterBot -p 8000
        
run_slave:
	java -SlaveBot -h localhost -p 8000
        
clean:
	$(RM) *.class
