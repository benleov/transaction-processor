# ME Bank coding challenge

### Build and Execution

To build the application:

```bash
./gradlew clean build
```

To run the application:

```bash
 ./gradlew run --args 'ACC334455,example/input.csv,20/10/2018T12:00:00,20/10/2018T19:00:00'
```

Where `ACC334455` is the account id, `example/input.csv` is the transaction file, and the remaining fields are
the start date and end date respectively. Please be aware that date format is `dd/MM/yyyy'T'HH:mm:ss`, and each field is separated by a comma.

### Assumptions

- Reversals reverse the matching transaction in full.

### Output

Prints the balance of transactions within the provided dates.
