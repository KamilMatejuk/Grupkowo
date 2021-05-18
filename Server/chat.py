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
    query = f'SELECT message_id, author_id, created, text FROM messages WHERE group_id == {group_id}'
    if start is not None and end is not None:
        start_date = datetime.strptime(start, '%d%m%Y')
        start_seconds = math.floor((start_date - datetime(1970, 1, 1)).total_seconds())
        end_date = datetime.strptime(end, '%d%m%Y')
        end_seconds = math.floor((end_date - datetime(1970, 1, 1)).total_seconds())
        start, end = min(start_seconds, end_seconds), max(start_seconds, end_seconds)
        query += f' AND created > {start} AND created < {end}'
        
    correct = executeQuery(query, objectKeys=['message_id', 'author_id', 'created', 'text'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {
        'messages': correct[:25]
    }


def getReactions(group_id: int, message_id: int, user):
    """Pobierz reakcje do danej wiadomości wraz z autorami

    Args:
        group_id (int): numer id grupy
        message_id (int): numer id wiadomości
        user  (dict): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection

    Returns:
        ResponseReactions: lista z reakcjami
    """
    checkGroupAccess(user, group_id)
    
    query = f'SELECT owner_id, reaction FROM reactions WHERE message_id == {message_id}'
    correct = executeQuery(query, objectKeys=['user_id', 'reaction'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {
        'reactions': correct
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


def addReaction(group_id: int, message_id: int, reaction: RequestReaction, user: RequestRegister):
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
            f'"{reaction.reaction}"'
        ])
    # already reacted with different reaction
    elif correct[0].get("reaction") != reaction.reaction:
        query = f'''
            UPDATE reactions 
            SET reaction = "{reaction.reaction}"
            WHERE message_id == {message_id} AND owner_id == {user.get("user_id")}
            '''
        
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
