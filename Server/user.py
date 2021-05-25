# external packages
import random
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
        HTTPException: nieprawidłowy adres email
        HTTPException: nazwa użytkownika lub email istnieje już w bazie

    Returns:
        dict: wygenerowany token
    """
    if '@' not in user.email or '.' not in user.email or len(user.email.split('@')) < 2 or len(user.email.split('.')) < 2:
        raise HTTPException(
            status_code=status.HTTP_406_NOT_ACCEPTABLE,
            detail='Incorrect email'
        )
    correct = insert('users', ['username', 'email', 'password'], [
        f'"{user.username}"',
        f'"{user.email}"',
        f'"{hashPassword(user.password)}"'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_406_NOT_ACCEPTABLE,
            detail='Email is already in database'
        )
    print("Successfully inserted user into database")
    token = generateToken(user.email, user.password)
    print(f"Successfully generated token {token}")
    return {
        'access_token': token,
        'token_type': 'bearer'
    }


def getGroupsAdmin(user):
    """Pobierz liste grup, gdzie użytkownik jest adminem

    Args:
        user (RequestRegister): aktualnie zalogowany użytkownik

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection

    Returns:
        ResponseUserGroups: lista grup
    """
    query = f'SELECT group_id, name, image FROM groups WHERE admin_id == {user.get("user_id")}'
    correct = executeQuery(query, objectKeys=['group_id', 'name', 'image'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {
        'groups': correct
    }


def getGroupsMember(user: RequestRegister):
    """Pobierz liste grup do których należy użytkownik

    Args:
        user (RequestRegister): aktualnie zalogowany użytkownik

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection

    Returns:
        ResponseUserGroups: lista grup
    """
    query = f'''
        SELECT user_group.group_id, groups.name, groups.image
        FROM user_group JOIN groups ON user_group.group_id = groups.group_id
        WHERE user_group.user_id == {user.get("user_id")}
        '''
    correct = executeQuery(query, objectKeys=['group_id', 'name', 'image'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {
        'groups': correct
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
    query = f'SELECT user_id, username, avatar FROM users WHERE user_id == {user_id}'
    correct = executeQuery(
        query, objectKeys=['user_id', 'username', 'avatar'])
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


def editProfile(newUser: RequestEditUser, currUser: RequestRegister):
    """Edytuj dane na swoim profilu

    Args:
        newUser (RequestEditUser): request z danymi do edycjy
        currUser (RequestRegister): aktualnie zalogowany użytkownik 

    Raises:
        HTTPException: błąd wykonywania polecenia SQL

    Returns:
        dict: empty response {}
    """
    fields = [(name, value) for (name, value) in newUser.dict().items() if value is not None]
    query = f'''
        UPDATE users 
        SET {", ".join(f'{f[0]} = "{f[1]}"' for f in fields)}
        WHERE user_id == {currUser.get("user_id")}
        '''
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}


def deleteProfile(user: RequestRegister):
    """Usuń swój profil

    Args:
        user (RequestRegister): aktualnie zalogowany użytkownik 

    Raises:
        HTTPException: błąd wykonywania polecenia SQL

    Returns:
        dict: empty response {}
    """
    query = f'DELETE FROM users WHERE user_id == {user.get("user_id")}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}

