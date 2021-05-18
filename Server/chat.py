# external packages
import math
from datetime import datetime
from fastapi import HTTPException, status
# internal packages
from models import *
from auth import checkGroupAccess
from database import insert, executeQuery

def getMessages(group_id: int, user, start=None, end=None):
    """Wyświetl wiadomości z zadanego okresu czasu

    Args:
        group_id (int): numer id grupy
        user  (dict): aktualnie zalogowany użytkownik (musi należeć do grupy)
        start (int, optional): początek zakresu - data postaci DDMMYYYY. Domyslnie zwróci ostatnie 25 wiadomości.
        end (int, optional): początek zakresu - data postaci DDMMYYYY. Domyslnie dzisiejszy dzień.

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection
        HTTPException: próba wyświetlenia danych nie przez członka grupy

    Returns:
        ResponseMessages: lista z danymi o wiadomościach
    """
    checkGroupAccess(user, group_id)
    # message data
    query = f'''
    SELECT messages.message_id, messages.created, messages.text, messages.author_id, users.username 
    FROM messages INNER JOIN users ON messages.author_id = users.user_id
    WHERE group_id == {group_id}
    '''
    if start is not None and end is not None:
        start_date = datetime.strptime(start, '%d%m%Y')
        start_seconds = math.floor((start_date - datetime(1970, 1, 1)).total_seconds())
        end_date = datetime.strptime(end, '%d%m%Y')
        end_seconds = math.floor((end_date - datetime(1970, 1, 1)).total_seconds())
        start, end = min(start_seconds, end_seconds), max(start_seconds, end_seconds)
        query += f' AND created > {start} AND created < {end}'
    correct = executeQuery(query, objectKeys=['message_id', 'created', 'text', 'author_id', 'author_username'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    
    # number of likes
    query = f'SELECT message_id, COUNT(reaction) FROM reactions GROUP BY message_id'
    correct2 = executeQuery(query, objectKeys=['message_id', 'count'])
    if isinstance(correct2, bool) and not correct2:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    for c in correct:
        likes = [int(i.get('count')) for i in correct2 if i.get('message_id') == c.get('message_id')] + [0]
        c['likes'] = likes[0]
    
    # if logged in user liked
    query = f'SELECT message_id, owner_id FROM reactions WHERE owner_id == {user.get("user_id")}'
    correct3 = executeQuery(query, objectKeys=['message_id', 'owner'])
    if isinstance(correct3, bool) and not correct3:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    for c in correct:
        liked = [True for i in correct3 if i.get('message_id') == c.get('message_id')] + [False]
        c['author_liked'] = liked[0]
        
    return {
        'messages': correct[:25]
    }


def addMessage(group_id: int, message: RequestCreateMessage, user: RequestRegister):
    """Wyślij wiadomość do czatu w danej grupie

    Args:
        group_id (int): numer id grupy
        message (RequestCreateMessage): dane wiadomości do utworzenia
        user (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL

    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)
    
    correct = insert('messages', ['group_id', 'author_id', 'created', 'text'], [
        f'{group_id}',
        f'{user.get("user_id")}',
        f'{math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds())}',
        f'"{message.text}"'
    ])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}


def addReaction(group_id: int, message_id: int, user: RequestRegister):
    """Dodaj reakcję na wiadomość w chacie danej grupy

    Args:
        group_id (int): numer id grupy
        message_id (int): numer id wiadomości
        reaction (RequestReaction): reakcja
        user  (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL
        HTTPException: błąd wykonywania polecenia SQL
        
    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)
    
    query = f'SELECT reaction FROM reactions WHERE message_id == {message_id} AND owner_id == {user.get("user_id")}'
    correct = executeQuery(query, objectKeys=['reaction'])
    if isinstance(correct, bool) and not correct or len(correct) < 0:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    
    # not yet reacted
    if len(correct) == 0:        
        correct = insert('reactions', ['message_id', 'owner_id', 'reaction'], [
            f'{message_id}',
            f'{user.get("user_id")}',
            '"like"'
        ])
        correct = executeQuery(query)
        if isinstance(correct, bool) and not correct:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail='Internal Server Error'
            )
    return {}


def deleteReaction(group_id: int, message_id: int, user: RequestRegister):
    """Dodaj reakcję na post w danej grupie

    Args:
        group_id (int): numer id grupy
        message_id (int): numer id wiadomości
        user  (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL
        HTTPException: błąd wykonywania polecenia SQL
        
    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)
    
    query = f'DELETE FROM reactions WHERE message_id == {message_id} AND owner_id == {user.get("user_id")}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}
