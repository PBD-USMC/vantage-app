# Personal Finance Management Android App

## Project Overview

This is a Personal Finance Management Android application developed for the SE3092 Platform Based Development assignment.

The app is designed to help users track income, expenses, savings goals, and financial history in one place. It focuses on users with irregular income sources, fragmented expenses, and a need to connect daily financial activity with long-term financial goals.

The application supports income tracking, expense tracking, dashboard summaries, goal progress monitoring, and transaction history.

## Main Features

- User registration and login using Firebase Authentication
- Dashboard summary for income, expenses, balance, and goal progress
- Multi-source income recording
- Expense recording with category, payment method, and expense type
- Committed and discretionary expense separation
- Savings goal creation and progress tracking
- History screen for viewing previous income and expense records
- Firestore cloud storage for user financial data
- Firestore security rules for user-based data protection

## Technology Stack

- Kotlin
- Jetpack Compose
- Material Design 3
- MVVM Architecture
- Firebase Authentication
- Firebase Firestore
- Navigation Compose
- Kotlin Coroutines
- StateFlow

## Firebase Configuration

To run the project with Firebase:

1. Create a Firebase project in Firebase Console.
2. Add an Android app using the package name:

```text
com.example.financeapp