import pymysql
import sys

database = pymysql.connect(host = 'localhost', user = 'encave', password = 'password', database = 'Encave')
cursor = database.cursor()
