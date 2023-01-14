# Simple Retry Library

Use this library when you want to repeat a method for a certain amount before giving up.

## 0. Table of Content

* [Simple Retry Library](#simple-retry-library)
    * [0. Table of Content](#0-table-of-content)
    * [1. To-do:](#1-to-do-)
    * [2. About](#2-about)

## 1. To-do:

- Return null in methods that return value?
- Think amounts returning Either / Optional.
- `Retry` class static final fields should take value from a config file.
- Add more optional features in the form of a Builder Pattern, like setting error messages

## 2. About

This library offers four methods to deal with situations,
when we want to retry certain operations (including methods that return value)
and do not fear that an unexpected Exception will ruin our plans.
