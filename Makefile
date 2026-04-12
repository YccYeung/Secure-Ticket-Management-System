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
	mvn spring-boot:run

# Phony targets
.PHONY: all clean compile run