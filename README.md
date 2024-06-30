# Secure Ticket Management System

## Overview

The Secure Ticket Management System is a Java-based application designed to facilitate the secure management of user registration, login, and ticket transactions for University of Wisconsin - Madison football games. The application focuses on robust security measures to protect sensitive user data, including passwords and credit card information.

## Features

- **User Registration and Authentication**
    - User registration with secure password hashing using BCrypt.
    - Secure credit card number storage using AES encryption.
    - User login with authentication by verifying hashed passwords and encrypted credit card numbers.

- **Payment Processing**
    - AES encryption for credit card numbers.
    - Tokenization to store and manage credit card tokens.
    - Simulation of payment authorization, capture, and refund processes.

- **Security Features**
    - Use of prepared statements with parameterized queries to prevent SQL injection attacks.
    - Secure storage of sensitive data such as passwords and credit card numbers.
    - Implementation of PCI-DSS compliance measures for secure payment processing.

- **User Dashboard**
    - Deposit money into the user account.
    - Display the UW-Madison football games schedule.
    - Buy and sell tickets for football games.
    - List current tickets held by the user.

- **Miscellaneous**
    - Input validation to ensure data integrity.
    - Error handling to manage invalid inputs and potential issues.

## Technologies Used

- **Java**
- **Maven**
- **MySQL**
- **Spring**
