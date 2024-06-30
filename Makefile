# Makefile for Secure-Ticket-Management-System

# Default target
all: clean compile run

# Clean the project
clean:
	mvn clean

# Compile the project
compile:
	mvn compile

# Run the project
run:
	mvn exec:java -Dexec.mainClass="TicketSystemFrontend"

# Phony targets
.PHONY: all clean compile run

