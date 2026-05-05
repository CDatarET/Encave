import pymysql
import sys

database = pymysql.connect(host = 'localhost', user = 'encave', password = 'password', database = 'Encave')
Cursor = database.cursor()
food_items = [sys.argv[3], sys.argv[4], sys.argv[5], sys.argv[6], sys.argv[7], sys.argv[8]]
total = 0

for i in range(len(food_items)):
    if i == 0:
        total += int(food_items[i]) * 109
    if i == 1:
        total += int(food_items[i]) * 120
    if i == 2:
        total += int(food_items[i]) * 80
    if i == 3:
        total += int(food_items[i]) * 90
    if i == 4:
        total += int(food_items[i]) * 250
    if i == 5:
        total += int(food_items[i]) * 150

Cursor.execute("call placeOrder(%s, %s, %s, 'UPI', @lastOrderID);",(sys.argv[1], sys.argv[2], str(total)))
        

print("Order placed successfully")