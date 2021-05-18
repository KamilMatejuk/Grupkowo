# external packages
import math
from datetime import datetime
from collections import Counter
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
    # post data
    query = f'''
        SELECT posts.post_id, posts.created, posts.text, posts.author_id, users.username
        FROM posts INNER JOIN users ON posts.author_id = users.user_id
        WHERE group_id == {group_id}
        '''
    if start is not None and end is not None:
        start_date = datetime.strptime(start, '%d%m%Y')
        start_seconds = math.floor((start_date - datetime(1970, 1, 1)).total_seconds())
        end_date = datetime.strptime(end, '%d%m%Y')
        end_seconds = math.floor((end_date - datetime(1970, 1, 1)).total_seconds())
        start, end = min(start_seconds, end_seconds), max(start_seconds, end_seconds)
        query += f' AND created > {start} AND created < {end}'
    correct = executeQuery(query, objectKeys=['post_id', 'created', 'text', 'author_id', 'author_username'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    
    # number of likes
    query = f'SELECT post_id, COUNT(reaction) FROM reactions GROUP BY post_id'
    correct2 = executeQuery(query, objectKeys=['post_id', 'count'])
    if isinstance(correct2, bool) and not correct2:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    for c in correct:
        likes = [int(i.get('count')) for i in correct2 if i.get('post_id') == c.get('post_id')] + [0]
        c['likes'] = likes[0]
    
    # if logged in user liked
    query = f'SELECT post_id, owner_id FROM reactions WHERE owner_id == {user.get("user_id")}'
    correct3 = executeQuery(query, objectKeys=['post_id', 'owner'])
    if isinstance(correct3, bool) and not correct3:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    for c in correct:
        liked = [True for i in correct3 if i.get('post_id') == c.get('post_id')] + [False]
        c['author_liked'] = liked[0]
        
    return {
        'posts': correct[:25]
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


def addReaction(group_id: int, post_id: int, user: RequestRegister):
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
    
    query = f'SELECT * FROM reactions WHERE post_id == {post_id} AND owner_id == {user.get("user_id")}'
    correct = executeQuery(query)
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
            '"like"'
        ])
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
