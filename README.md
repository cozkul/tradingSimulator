# Trading Simulator

## Introduction

The ETF Timing Simulator is a robust Java desktop application designed to empower users in simulating timing their financial investments with ease. In an ever-evolving financial landscape, having a tool that provides education and an opportunity to try timing the market without real life consequences is invaluable.

Students seeking an understanding of day trading in the stock market are the intended users for this project,

I started this project because I want to demonstrate that if stock prices are a random walk, it is not possible to beat the average consistently.

## User Stories

### 1. View Account
- *As a user*, I want to be able to **view my balance and positions**, *so that* I can have a clear overview of my current financial standing.

### 2. Get Quotes for Existing Securities
- *As a user*, I want to be able to **get quote for existing securities**, *so that* know the exact buy and sell price.

### 3. View Security History
- *As a user*, I want to be able to **view the market history**, including past prices, *so that* I can track the market over time and make informed decisions.

### 4. Buy or Sell Securities
- *As a user*, I want to be able to **buy or sell stocks or securities**, *so that* I can simulate simple market timing strategies optimize my portfolio.

### 5. Add New Securities
- *As a user*, I want to be able to **add new simulated securities**, *so that* I can simulate diversifying my portfolio.

### 6. List Existing Securities
- *As a user*, I want to be able to **view existing securities**, *so that* I can remember which ETFs are being simulated.

### 7. Save Simulation
- *As a user*, I want to be able to **save the simulation**, *so that* I continue the simulation at a later date if I so choose.

### 8. Load Simulation
- *As a user*, I want to be able to **load the simulation**, *so that* I continue with simulation if I choose so.

## Instructions for Grader
- You can generate the first required action related to adding Security to an Account by using menu "Simulation > Create New Security" or Ctrl + X. Input in the dialog box must be valid doubles.
- You can generate the second required action related to buying Security with Account by using Order JPanel. Select desired security in market JList, select desired radiobutton action, enter a valid integer to Quantity field and click Execute button.
- You can locate my visual component by looking at the chart JPanel. You must select desired security in market JList and click view security in chart checkbox.
- You can save the state of my application by using menu "File > Save File" or Ctrl + S.
- You can reload the state of my application by using menu "File > Load File" or Ctrl + O.
- You can create new account by using menu "File > New Account" or Ctrl + N. Input must be valid String and double.