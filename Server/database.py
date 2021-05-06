import math
import sqlite3
from pathlib import Path
from datetime import datetime


# return a path to database file
def getDatabasePath():
    db_path = Path(__file__).absolute().parent.parent.joinpath('DB')
    db_file = db_path.joinpath('grupkowo_v1.db')
    return db_file


# create all tables
def initialize():
    connection = sqlite3.connect(getDatabasePath())
    # user
    connection.execute('''
        CREATE TABLE IF NOT EXISTS users (
            user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL,
            first_name TEXT NOT NULL,
            last_name TEXT NOT NULL,
            email TEXT NOT NULL,
            password TEXT NOT NULL,
            avatar BLOB
        ) ''')
    # group
    connection.execute('''
        CREATE TABLE IF NOT EXISTS groups (
            group_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            admin_id INTEGER NOT NULL,
            image BLOB,
            FOREIGN KEY (admin_id) REFERENCES users (user_id)
        ) ''')
    # user group n-n connection
    connection.execute('''
        CREATE TABLE IF NOT EXISTS user_group (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            group_id INTEGER NOT NULL,
            FOREIGN KEY (user_id) REFERENCES users (user_id),
            FOREIGN KEY (group_id) REFERENCES groups (group_id)
        ) ''')
    # messages
    connection.execute('''
        CREATE TABLE IF NOT EXISTS messages (
            message_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            group_id INTEGER NOT NULL,
            author_id INTEGER NOT NULL,
            created INTEGER NOT NULL,
            text TEXT NOT NULL,
            FOREIGN KEY (group_id) REFERENCES groups (group_id),
            FOREIGN KEY (author_id) REFERENCES users (user_id)
        ) ''')
    # posts
    connection.execute('''
        CREATE TABLE IF NOT EXISTS posts (
            post_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            group_id INTEGER NOT NULL,
            author_id INTEGER NOT NULL,
            created INTEGER NOT NULL,
            text TEXT NOT NULL,
            FOREIGN KEY (group_id) REFERENCES groups (group_id),
            FOREIGN KEY (author_id) REFERENCES users (user_id)
        ) ''')
    # comments
    connection.execute('''
        CREATE TABLE IF NOT EXISTS comments (
            comment_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            post_id INTEGER NOT NULL,
            author_id INTEGER NOT NULL,
            created INTEGER NOT NULL,
            text TEXT NOT NULL,
            FOREIGN KEY (post_id) REFERENCES posts (post_id),
            FOREIGN KEY (author_id) REFERENCES users (user_id)
        ) ''')
    # reactions
    connection.execute('''
        CREATE TABLE IF NOT EXISTS reactions (
            id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            post_id INTEGER,
            message_id INTEGER,
            owner_id INTEGER NOT NULL,
            reaction TEXT NOT NULL,
            FOREIGN KEY (post_id) REFERENCES posts (post_id),
            FOREIGN KEY (message_id) REFERENCES messages (message_id),
            FOREIGN KEY (owner_id) REFERENCES users (user_id)
        ) ''')
    
    connection.commit()
    connection.close()


def insert(table, columns, args):
    connection = sqlite3.connect(getDatabasePath())
    query = f'''
        INSERT INTO {table} ({
        ",".join(str(c) for c in columns)
        }) VALUES ({
        ",".join(str(a) for a in args)
        })'''
    connection.execute(query)
    connection.commit()
    connection.close()


def createDummyData():
    # create 9 users
    for i in range(1, 10):
        insert('users', ['username','first_name', 'last_name', 'email', 'password'], [
            f'"username{i}"',
            f'"Name{i}"',
            f'"Surname{i}"',
            f'"user{i}gmail.com"',
            f'"password{i}"' ])
    # create groups
    insert('groups', ['name', 'admin_id'], ['"Super Grupa 1"', 1])
    insert('groups', ['name', 'admin_id'], ['"Super Grupa 2"', 1])
    insert('groups', ['name', 'admin_id'], ['"Super Grupa 3"', 2])
    insert('groups', ['name', 'admin_id'], ['"Super Grupa 4"', 3])
    insert('groups', ['name', 'admin_id'], ['"Super Grupa 5"', 4])
    # add users to groups
    insert('user_group', ['user_id', 'group_id'], [2, 1])
    insert('user_group', ['user_id', 'group_id'], [3, 1])
    insert('user_group', ['user_id', 'group_id'], [4, 2])
    insert('user_group', ['user_id', 'group_id'], [5, 4])
    insert('user_group', ['user_id', 'group_id'], [6, 4])
    insert('user_group', ['user_id', 'group_id'], [7, 4])
    insert('user_group', ['user_id', 'group_id'], [8, 4])
    insert('user_group', ['user_id', 'group_id'], [9, 4])
    insert('user_group', ['user_id', 'group_id'], [6, 3])
    # send messages to chats in groups
    insert('messages', ['group_id', 'author_id', 'created', 'text'], [
        1, 2, math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds()), '"Świetna wiadomość"'])
    insert('messages', ['group_id', 'author_id', 'created', 'text'], [
        1, 2, math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds()) + 10, '"Masz raka"'])
    insert('messages', ['group_id', 'author_id', 'created', 'text'], [
        1, 2, math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds()) + 25, '":)"'])
    insert('messages', ['group_id', 'author_id', 'created', 'text'], [
        5, 4, math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds()) + 5685, '"Jestem adminem!"'])
    # add posts
    insert('posts', ['group_id', 'author_id', 'created', 'text'], [
        4, 6, math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds()), '"Post organizacyjny"'])
    insert('posts', ['group_id', 'author_id', 'created', 'text'], [
        4, 8, math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds()) + 10, '"Twoja stara"'])
    insert('posts', ['group_id', 'author_id', 'created', 'text'], [
        3, 6, math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds()) + 25, '"Sprzedam opla"'])
    insert('posts', ['group_id', 'author_id', 'created', 'text'], [
        5, 4, math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds()) - 26485, '"Smoke weed everyday"'])
    # add comments
    insert('comments', ['post_id', 'author_id', 'created', 'text'], [
        1, 5, math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds()) + 50, '"Thats a nice post"'])
    insert('comments', ['post_id', 'author_id', 'created', 'text'], [
        2, 6, math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds()) + 1265, '"go fuck yourself"'])
    # add reactions
    insert('reactions', ['post_id', 'owner_id', 'reaction'], [1, 6, '"like"'])
    insert('reactions', ['post_id', 'owner_id', 'reaction'], [1, 8, '"love"'])
    insert('reactions', ['message_id', 'owner_id', 'reaction'], [1, 6, '"hate"'])
        


if __name__ == '__main__':
    initialize()
    createDummyData()