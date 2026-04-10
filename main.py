import pymysql

database = pymysql.connect(host = 'localhost', user = 'encave', password = 'password')
Cursor = database.cursor()

Cursor.execute("create database if not exists Encave;")
Cursor.execute("use Encave;")
Cursor.execute("""create table Customer(
    prn int primary key, 
    firstName varchar(50) not null, 
    lastName varchar(50) not null,
    email varchar(100) not null unique,
    phone int not null unique,
    password varchar(50) not null,
    creation datetime default current_timestamp);
    """)

print("successfully created a table")
database.commit()