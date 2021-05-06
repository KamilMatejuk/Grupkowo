# external packages
import math
from datetime import datetime
from fastapi import HTTPException, status
# internal packages
from models import *
from auth import checkGroupAccess
from database import executeQuery


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
