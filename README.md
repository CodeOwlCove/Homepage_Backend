# Database

If the volume of the database is newly created (and thus has no backend_user), create a new user with the following commands:
> CREATE USER 'newuser'@'%' IDENTIFIED BY 'password';

And then grand priviliges with

> GRANT ALL PRIVILEGES ON *.* TO 'newuser'@'%';

Then you should be able to connect to the database with the new user.