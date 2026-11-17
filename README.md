The "gs" in the repo name stands for "Getting Started": this project began as a copy of
Spring's official Getting Started guide for Spring Batch ([spring-guides/gs-batch-processing](https://github.com/spring-guides/gs-batch-processing)).
The original guide text is preserved in README-orig.adoc, and the 'complete' directory comes from the guide's layout.

0. (cd complete; mvn eclipse:eclipse) and then import 'complete' directory as project into Eclipse

1. Install secrets.properties at 'complete' project top level.  (It is not provided for obvious reasons)
It should contain valid property values for the following properties:
oracle.database.url=...
oracle.database.user=...
oracle.database.password=...

2. Run reset-small.sql against the database (assumes that the table exists)
This will insert 10,000 rows into the table.

3. Run select.sql and verify that 900+ records are returned.

4. Run the batch application, it should take about 1 minute to run.

5. Run select.sql and verify that 0 records are returned.
