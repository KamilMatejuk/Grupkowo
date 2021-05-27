# external packages
from fastapi import HTTPException, status
# internal packages
from models import *
from auth import checkGroupAccess
from database import insert, executeQuery


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


def getUsers(group_id: int, user: RequestRegister):
    """Wyświetl listę członków grupy (bez admina)

    Args:
        group_id (int): numer id grupy
        user  (RequestRegister): aktualnie zalogowany użytkownik (musi należeć do grupy)

    Raises:
        HTTPException: jakiś nieoczekiwany błąd - sql injection
        HTTPException: próba wyświetlenia danych nie przez członka grupy

    Returns:
        ResponseGroup: dict z danymi użytkownika
    """
    checkGroupAccess(user, group_id)
    query = f'''
        SELECT user_group.user_id, users.username, users.avatar
        FROM user_group JOIN users ON user_group.user_id = users.user_id
        WHERE user_group.group_id == {group_id}
        '''
    correct = executeQuery(query, objectKeys=['user_id', 'username', 'avatar'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {
        'users': correct
    }


def createGroup(group: RequestCreateGroup, user: RequestRegister):
    """Utwórz grupę

    Args:
        group (RequestCreateGroup): dane nowej grupy
        user (RequestRegister): aktualnie zalogowany użytkownik

    Raises:
        HTTPException: błąd wykonywania polecenia SQL

    Returns:
        dict: empty response {}
    """
    cols = ['name', 'admin_id']
    vals = [f'"{group.name}"', f'{user.get("user_id")}']
    if (group.image is not None):
        cols.append('image')
        vals.append(f'"{group.image}"')
    
    correct = insert('groups', cols, vals)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}


def deleteGroup(group_id: int, user: RequestRegister):
    """Usuń swoją grupę

    Args:
        group_id (int): numer grupy do usunięcia
        user (RequestRegister): aktualnie zalogowany użytkownik

    Raises:
        HTTPException: błąd wykonywania polecenia SQL

    Returns:
        dict: empty response {}
    """
    query = f'DELETE FROM groups WHERE admin_id == {user.get("user_id")} AND group_id == {group_id}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    query = f'DELETE FROM user_group WHERE group_id == {group_id}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    
    return {}


def addUser(group_id: int, user: RequestAddUser, currUser: RequestRegister):
    """Dodanie innego użytkownika do swojej grupy

    Args:
        group_id (int): numer id grupy
        user (RequestAddUser): dane użytkownika do dodania
        currUser (RequestRegister): aktualnie zalogowany użytkownik

    Raises:
        HTTPException: błąd wykonywania polecenia SQL
        HTTPException: próba dodania użytkownika do nie swojej grupy
        HTTPException: błąd wykonywania polecenia SQL

    Returns:
        dict: empty response {}
    """
    query = f'SELECT admin_id FROM groups WHERE group_id == {group_id}'
    correct = executeQuery(query, objectKeys=['admin_id'])
    if isinstance(correct, bool) and not correct or len(correct) == 0:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    if int(correct[0].get("admin_id")) != int(currUser.get("user_id")):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail='User is not admin of this group'
        )
    
    correct = insert('user_group', ['user_id', 'group_id'], [
        f'{user.user_id}',
        f'{group_id}',
    ])
    if isinstance(correct, bool) and not correct or len(correct) == 0:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}


def deleteUser(group_id: int, user_id: int, currUser: RequestRegister):
    """Usunięcie użytkownika ze swojej grupy

    Args:
        group_id (int): numer id grupy
        user_id (int): numer id użytkownika do usunięcia
        currUser (RequestRegister): aktualnie zalogowany użytkownik

    Raises:
        HTTPException: błąd wykonywania polecenia SQL
        HTTPException: próba usunięcia użytkownika z nie swojej grupy
        HTTPException: błąd wykonywania polecenia SQL

    Returns:
        dict: empty response {}
    """
    query = f'SELECT admin_id FROM groups WHERE group_id == {group_id}'
    correct = executeQuery(query, objectKeys=['admin_id'])
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    if len(correct) == 0 or int(correct[0].get("admin_id")) != int(currUser.get("user_id")):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail='User is not admin of this group'
        )
    
    query = f'DELETE FROM user_group WHERE group_id == {group_id} AND user_id == {user_id}'
    correct = executeQuery(query)
    if isinstance(correct, bool) and not correct:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    return {}
