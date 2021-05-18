# external packages
import math
from datetime import datetime
from fastapi import HTTPException, status
# internal packages
from models import *
from auth import checkGroupAccess
from database import insert, executeQuery

def getPosts(group_id: int, user: RequestRegister, start=None, end=None):
    """Wyświetl posty z zadanego okresu czasu

    Args:
        group_id (int): numer id grupy
        user  (dict): aktualnie zalogowany użytkownik (musi należeć do grupy)
        start (int, optional): początek zakresu - data postaci DDMMYYYY. Domyslnie zwróci ostatnie 25 postów.
        end (int, optional): początek zakresu - data postaci DDMMYYYY. Domyslnie dzisiejszy dzień.

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection
        HTTPException: próba wyświetlenia danych nie przez członka grupy

    Returns:
        ResponsePosts: lista z danymi o postach
    """
    checkGroupAccess(user, group_id)
    
    query = f'SELECT post_id, author_id, created, text FROM posts WHERE group_id == {group_id}'
    if start is not None and end is not None:
        start_date = datetime.strptime(start, '%d%m%Y')
        start_seconds = math.floor((start_date - datetime(1970, 1, 1)).total_seconds())
        end_date = datetime.strptime(end, '%d%m%Y')
        end_seconds = math.floor((end_date - datetime(1970, 1, 1)).total_seconds())
        start, end = min(start_seconds, end_seconds), max(start_seconds, end_seconds)
        query += f' AND created > {start} AND created < {end}'
        
    correct = executeQuery(query, objectKeys=['post_id', 'author_id', 'created', 'text'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {
        'posts': correct[:25]
    }


def getReactions(group_id: int, post_id: int, user: RequestRegister):
    """Pobierz reakcje do danego posta wraz z autorami

    Args:
        group_id (int): numer id grupy
        post_id (int): numer id posta
        user  (dict): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection

    Returns:
        ResponseReactions: lista z reakcjami
    """
    checkGroupAccess(user, group_id)
    
    query = f'SELECT owner_id, reaction FROM reactions WHERE post_id == {post_id}'
    correct = executeQuery(query, objectKeys=['user_id', 'reaction'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {
        'reactions': correct
    }


def createPost(group_id: int, post: RequestCreatePost, user: RequestRegister):
    """Utwórz post w danej grupie

    Args:
        group_id (int): numer id grupy
        post (RequestCreatePost): dane posta do utworzenia
        user (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL

    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)
    
    correct = insert('posts', ['group_id', 'author_id', 'created', 'text'], [
        f'{group_id}',
        f'{user.get("user_id")}',
        f'{math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds())}',
        f'"{post.text}"'
    ])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}


def editPost(group_id: int, post_id: int, post: RequestCreatePost, user: RequestRegister):
    """Edytuj swój post w danej grupie

    Args:
        group_id (int): numer id grupy
        post_id (int): numer id posta
        post (RequestCreatePost): zmienione dane posta
        user  (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL
        HTTPException: próba edycji nie swojego posta
        HTTPException: błąd wykonywania polecenia SQL
        
    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)
        
    query = f'SELECT * FROM posts WHERE post_id == {post_id} AND author_id == {user.get("user_id")}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct or len(correct) < 0:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    if len(correct) == 0:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail='User isn\'t author of this post'
        )
    
    fields = [(name, value) for (name, value) in post.dict().items() if value is not None]
    query = f'''
        UPDATE posts 
        SET {", ".join(f'{f[0]} = "{f[1]}"' for f in fields)}
        WHERE post_id == {post_id}
        '''
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}


def deletePost(group_id: int, post_id: int, user: RequestRegister):
    """Usuń swój post w danej grupie

    Args:
        group_id (int): numer id grupy
        post_id (int): numer id posta
        user  (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL
        HTTPException: próba usunięcia nie swojego posta
        HTTPException: błąd wykonywania polecenia SQL
        
    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)
        
    query = f'SELECT * FROM posts WHERE post_id == {post_id} AND author_id == {user.get("user_id")}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct or len(correct) < 0:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    if len(correct) == 0:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail='User isn\'t author of this post'
        )
    
    query = f'DELETE FROM posts WHERE post_id == {post_id}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}


def addReaction(group_id: int, post_id: int, reaction: RequestReaction, user: RequestRegister):
    """Dodaj reakcję na post w danej grupie

    Args:
        group_id (int): numer id grupy
        post_id (int): numer id posta
        reaction (RequestReaction): reakcja
        user  (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL
        HTTPException: błąd wykonywania polecenia SQL
        
    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)
    
    query = f'SELECT reaction FROM reactions WHERE post_id == {post_id} AND owner_id == {user.get("user_id")}'
    correct = executeQuery(query, objectKeys=['reaction'])
    if isinstance(correct, bool) and not correct or len(correct) < 0:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    
    # not yet reacted
    if len(correct) == 0:        
        correct = insert('reactions', ['post_id', 'owner_id', 'reaction'], [
            f'{post_id}',
            f'{user.get("user_id")}',
            f'"{reaction.reaction}"'
        ])
    # already reacted with different reaction
    elif correct[0].get("reaction") != reaction.reaction:
        query = f'''
            UPDATE reactions 
            SET reaction = "{reaction.reaction}"
            WHERE post_id == {post_id} AND owner_id == {user.get("user_id")}
            '''
        
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}


def deleteReaction(group_id: int, post_id: int, user: RequestRegister):
    """Dodaj reakcję na post w danej grupie

    Args:
        group_id (int): numer id grupy
        post_id (int): numer id posta
        user  (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL
        HTTPException: błąd wykonywania polecenia SQL
        
    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)
    
    query = f'DELETE FROM reactions WHERE post_id == {post_id} AND owner_id == {user.get("user_id")}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}
