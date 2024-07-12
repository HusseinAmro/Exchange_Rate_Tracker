# Exchange_Rate_Tracker

This project is part of the EECE 430L – Web, Mobile, and Application Development Lab course at the American University of Beirut (AUB). Throughout this course, each student individually developed a comprehensive currency exchange platform, starting from a basic website to fully functional web, mobile, and desktop applications, integrated with a backend server. 

## Overview

The main objective of this project is to create a multi-platform application that allows users to input and track LBP-USD exchange transactions. Users can view current exchange rates, calculate the value of possible exchanges, and manage their transaction history. The project incorporates web development, backend server implementation, mobile and desktop application development, and user authentication.

## Features

The following features are supported across all platforms (Web, Mobile, and Desktop). Please note that the ordering or visual representation may vary slightly to accommodate the unique aspects of each platform.

- **Core Functionality:**
    - **Multi-Platform Transaction Input and Display:** Users can input LBP-USD exchange transactions across web, mobile, and desktop applications. The average exchange rate is displayed accordingly.
    - **Current Exchange Rates Display:** Users can view up-to-date exchange rates and register new transactions seamlessly.
    - **Transaction Reporting:** Users can report completed transactions, with the system displaying the current exchange rate at the time of the transaction.
    - **Exchange Rate Calculator:** A built-in calculator helps users determine the value of potential exchanges based on the latest rates.

- **Backend and Data Management**
    - **Backend Server Integration:** A robust backend server supports web clients in adding exchange transactions and querying current exchange rates through an HTTP RESTful API.
    - **Centralized Data Management:** All platforms read from and write data to a central server, ensuring consistency across devices.
    - **Cumulative Moving Average Calculation:** Exchange rates are calculated using a cumulative moving average of transactions from the past three days. This provides an equi-weighted average of all transactions within the last 72 hours.

- **User Accounts and Transactions**
    - **User Accounts and Authentication:** Users can create accounts with unique usernames and passwords, enabling them to perform transactions either anonymously or through their account.
    - **User-Associated Transactions:** Transactions can be linked to user accounts, allowing users to fetch and view their transaction history in a table format upon logging in.
    - **Detailed Transaction Input:** When adding a transaction, users specify the USD and LBP amounts, the transaction direction, and optionally link it to their account if logged in.
