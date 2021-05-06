# external packages
from fastapi import HTTPException, status
# internal packages
from models import *
from auth import checkGroupAccess
from database import executeQuery


def getGroup(group_id: int):
    """Wyświetl dane o innej grupie

    Args:
        group_id (int): numer id grupy

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection
        HTTPException: podany numer id nie istnieje w bazie

    Returns:
        ResponseGroup: dict z danymi użytkownika
    """
    query = f'SELECT name, admin_id, image FROM groups WHERE group_id == {group_id}'
    correct = executeQuery(query, objectKeys=['name', 'admin_id', 'image'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    if len(correct) == 0:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail='Group not found'
        )
    return correct[0]


def getUsers(group_id: int, user):
    """Wyświetl listę członków grupy (bez admina)

    Args:
        group_id (int): numer id grupy
        user  (dict): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection
        HTTPException: próba wyświetlenia danych nie przez członka grupy

    Returns:
        ResponseGroup: dict z danymi użytkownika
    """    
    return {
        'users': checkGroupAccess(user, group_id),
    }
