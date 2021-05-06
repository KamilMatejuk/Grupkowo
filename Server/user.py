# external packages
from fastapi import HTTPException, status
# internal packages
from models import *
from database import insert, executeQuery
from auth import generateToken, hashPassword


def login(username: str, password: str):
    """Zalogowanie użytkownika do systemu

    Args:
        username (str): nazwa użytkownika lub email
        password (str): niezahashowane hasło

    Returns:
        dict: wygenerowany token
    """
    token = generateToken(username, password)
    return {
        'access_token': token,
        'token_type': 'bearer'
    }


def register(user: RequestRegister):
    """Utworzenie konta dla uzytkownika i zalogowanie go do systemu

    Args:
        user (RequestRegister): otrzymany obiekt użytkownika z requesta

    Raises:
        HTTPException: nazwa użytkownika lub email istnieje już w bazie

    Returns:
        dict: wygenerowany token
    """
    correct = insert('users', ['username','first_name', 'last_name', 'email', 'password'], [
        f'"{user.username}"',
        f'"{user.first_name}"',
        f'"{user.last_name}"',
        f'"{user.email}"',
        f'"{hashPassword(user.password)}"'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_406_NOT_ACCEPTABLE, 
            detail='Username/email is already in database'
        )
    token = generateToken(user.username, user.password)
    return {
        'access_token': token,
        'token_type': 'bearer'
    }


def getGroupsAdmin(user):
    """Pobierz liste grup, gdzie użytkownik jest adminem
    
    Args:
        user (dict): aktualnie zalogowany użytkownik

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection

    Returns:
        ResponseUserGroups: lista grup
    """
    query = f'SELECT group_id FROM groups WHERE admin_id == {user.get("user_id")}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, 
            detail='Internal Server Error'
        )
    return {
        'groups': [id[0] for id in correct]
    }


def getGroupsMember(user: RequestRegister):
    """Pobierz liste grup do których należy użytkownik
    
    Args:
        user (dict): aktualnie zalogowany użytkownik

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection

    Returns:
        ResponseUserGroups: lista grup
    """
    query = f'SELECT group_id FROM user_group WHERE user_id == {user.get("user_id")}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, 
            detail='Internal Server Error'
        )
    return {
        'groups': [id[0] for id in correct]
    }


def showProfile(user_id: int):
    """Wyświetl dane o innym użytkowniku

    Args:
        user_id (int): numer id użytkownika

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection
        HTTPException: podany numer id nie istnieje w bazie

    Returns:
        ResponseCollegueProfile: dict z danymi użytkownika
    """
    query = f'SELECT username, first_name, last_name, avatar FROM users WHERE user_id == {user_id}'
    correct = executeQuery(query, objectKeys=['username', 'first_name', 'last_name', 'avatar'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, 
            detail='Internal Server Error'
        )
    if len(correct) == 0:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, 
            detail='User not found'
        )
    return correct[0]
