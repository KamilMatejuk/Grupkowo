# external packages
import math
from datetime import datetime
from fastapi import HTTPException, status
# internal packages
from models import *
from auth import checkGroupAccess
from database import insert, executeQuery


def getComments(group_id: int, post_id: int, user):
    """Pobierz komentarze pod danym postem

    Args:
        group_id (int): numer id grupy
        post_id (int): numer id posta
        user  (dict): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection

    Returns:
        ResponseComments: lista z komentarzami
    """
    checkGroupAccess(user, group_id)
    query = f'SELECT comment_id, author_id, created, text FROM comments WHERE post_id == {post_id}'
    correct = executeQuery(query, objectKeys=['comment_id', 'author_id', 'created', 'text'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {
        'comments': correct
    }

def addComment(group_id: int, post_id: int, comment: RequestCreateComment, user: RequestRegister):
    """Dodaj komentarz do posta

    Args:
        group_id (int): numer id grupy
        post_id (int): numer id posta
        comment (RequestCreateComment): dane komentarza do utworzenia
        user (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL

    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)
    
    correct = insert('comments', ['post_id', 'author_id', 'created', 'text'], [
        f'{post_id}',
        f'{user.get("user_id")}',
        f'{math.floor((datetime.now() - datetime(1970, 1, 1)).total_seconds())}',
        f'"{comment.text}"'
    ])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}


def editComment(group_id: int, post_id: int, comment_id: int, comment: RequestCreateComment, user: RequestRegister):
    """Edytuj swój post w danej grupie

    Args:
        group_id (int): numer id grupy
        post_id (int): numer id posta
        comment_id (int): numer id komentarza
        post (RequestCreatePost): zmienione dane posta
        user  (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL
        HTTPException: próba edycji nie swojego komentarza
        HTTPException: błąd wykonywania polecenia SQL
        
    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)
        
    query = f'SELECT * FROM comments WHERE comment_id == {comment_id} AND author_id == {user.get("user_id")}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct or len(correct) < 0:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    if len(correct) == 0:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail='User isn\'t author of this comment'
        )
    
    fields = [(name, value) for (name, value) in comment.dict().items() if value is not None]
    query = f'''
        UPDATE comments 
        SET {", ".join(f'{f[0]} = "{f[1]}"' for f in fields)}
        WHERE comment_id == {comment_id}
        '''
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}


def deleteComment(group_id: int, post_id: int, comment_id: int, user: RequestRegister):
    """Usuń swój post w danej grupie

    Args:
        group_id (int): numer id grupy
        post_id (int): numer id posta
        comment_id (int): numer id komentarza
        user  (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: błąd wykonywania polecenia SQL
        HTTPException: próba usunięcia nie swojego komentarza
        HTTPException: błąd wykonywania polecenia SQL
        
    Returns:
        dict: empty response {}
    """
    checkGroupAccess(user, group_id)

    query = f'SELECT * FROM comments WHERE comment_id == {comment_id} AND author_id == {user.get("user_id")}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct or len(correct) < 0:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    if len(correct) == 0:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail='User isn\'t author of this comment'
        )
    
    query = f'DELETE FROM comments WHERE comment_id == {comment_id}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}

