# external packages
import math
import sqlite3
import traceback
from pathlib import Path
from datetime import datetime
from bcrypt import hashpw, gensalt


def getDatabasePath():
    """Plik bazy danych sqlite3

    Returns:
        str: ścieżka absolutna do pliku
    """
    db_path = Path(__file__).absolute().parent.parent.joinpath('DB')
    db_file = db_path.joinpath('grupkowo_v1.db')
    return db_file


def initialize():
    """Stworzenie pustych tabeli w bazie danych
    """
    connection = sqlite3.connect(getDatabasePath())
    # user
    connection.execute('''
        CREATE TABLE IF NOT EXISTS users (
            user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            email TEXT NOT NULL UNIQUE,
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


def insert(table: str, columns, args):
    """Wstawienie wartości do odpowiedniej tabeli

    Args:
        table (str): nazwa tabeli
        columns (list(str)): lista nazw kolumn w tabeli
        args (list(str)): lista wartości odpowiadających kolumnom

    Returns:
        list: pusta lista, lub False jeżeli napotkano błąd
    """
    query = f'''
        INSERT INTO {table} ({
        ",".join(str(c) for c in columns)
        }) VALUES ({
        ",".join(str(a) for a in args)
        })'''
    return executeQuery(query)


def createDummyData():
    """Utworzenie danych testowych
    """
    # create 9 users
    for i in range(1, 10):
        insert('users', ['username', 'email', 'password'], [
            f'"username{i}"',
            f'"user{i}@gmail.com"',
            f'"{hashpw(str(f"password{i}").encode("utf-8"), gensalt())}"' ])
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
        

def executeQuery(query, objectKeys=[]):
    """Wywołanie polecenia na głównej bazie danych

    Args:
        query (str): polecenie SQL
        objectKeys (list, optional): lista kluczy, którym przypisać znalezione wartości, jeżeli otrzymany efekt ma być listą dict

    Returns:
        list: lista tuple lub dict, w zalezności od objectKeys
    """
    results = []
    try:
        connection = sqlite3.connect(getDatabasePath())
        cursor = connection.cursor()
        cursor.execute(query)
        for row in cursor.fetchall():
            if len(objectKeys) == len(row) != 0:
                obj = {}
                for i in range(len(objectKeys)):
                    obj[objectKeys[i]] = row[i]
                results.append(obj)
            else:
                results.append(row)
        connection.commit()
        connection.close()
        return results
    except sqlite3.Error:
        traceback.print_exc()
        return False
    except:
        traceback.print_exc()
        return False
    


if __name__ == '__main__':
    initialize()
    createDummyData()
