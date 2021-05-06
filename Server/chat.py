# external packages
import math
from datetime import datetime
from fastapi import HTTPException, status
# internal packages
from models import *
from auth import checkGroupAccess
from database import executeQuery

def getChats(group_id: int, user, start=None, end=None):
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
