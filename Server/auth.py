# extrenal packages
import jwt
import traceback
from bcrypt import hashpw, checkpw, gensalt
from fastapi.security import OAuth2PasswordBearer
from fastapi import Depends, HTTPException, status
# internal packages
from database import executeQuery
from models import *


oauth2_scheme = OAuth2PasswordBearer(tokenUrl='token')
JWT_SECRET = 'myjwtsecret'


def hashPassword(plain_text_pass: str):
    """Wygenerowanie zahashowanego hasła

    Args:
        plain_text_pass (str): hasło

    Returns:
        str: hash hasła
    """
    return hashpw(plain_text_pass.encode('utf8'), gensalt()).decode('utf-8')


def checkPassword(plain_text_pass: str, hashed_pass: str):
    """Porównanie kandydata na hasło z zahashowanym hasłem

    Args:
        plain_text_pass (str): hasło
        hashed_pass (str): zahashowane hasło

    Returns:
        bool: hasła sobie odpowiadają
    """
    try:
        encoded_plain_text_pass = plain_text_pass.encode('utf8')
        encoded_hashed_pass = hashed_pass.encode('utf8')
        return checkpw(encoded_plain_text_pass, encoded_hashed_pass)
    except:
        traceback.print_exc()
        return False


def generateToken(username: str, password: str):
    """Stworzenie tokena JWT dla uzytkownia

    Args:
        username (str): nazwa użytkownika lub email
        password (str): hasło niezahashowane

    Raises:
        HTTPException: nie istnieje uzytkownik o tych danych

    Returns:
        str: jwt token
    """
    # authenticate user
    hashed_pass = hashPassword(password)
    user = [user for user in 
            executeQuery(f'SELECT user_id, password FROM users WHERE username == "{username}" OR email == "{username}"')
            if checkPassword(password, user[1])]
    if len(user) == 0:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, 
            detail='Incorrect username/email or password'
        )
    # create token
    token = jwt.encode({'id': int(user[0][0])}, JWT_SECRET)
    return token


async def getCurrentUser(token: str = Depends(oauth2_scheme)):
    """Pobranie aktualnie zalogowanego użytkownika

    Args:
        token (str): token przekazany w nagłowku zapytania

    Raises:
        HTTPException: nie istnieje użytkonik o takim tokenie

    Returns:
        dict: obiekt użytkownika
    """
    payload = jwt.decode(token, JWT_SECRET, algorithms=['HS256'])
    user = executeQuery(
        f'SELECT user_id, username, email, avatar FROM users WHERE user_id == {payload.get("id")}',
        objectKeys=['user_id', 'username', 'email', 'avatar'])
    if len(user) == 0:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, 
            detail='Unauthorized request'
        )
    return user[0]


def checkGroupAccess(user, group_id):
    """Sprawdzenie czy użytkownik ma dostęp do danej grupy

    Args:
        user ([type]): [description]

    Raises:
        HTTPException: [description]
        HTTPException: [description]

    Returns:
        [type]: [description]
    """
    query = f'SELECT admin_id FROM groups WHERE group_id == {group_id}'
    correct1 = executeQuery(query)
    if isinstance(correct1, bool) and not correct1:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    query = f'SELECT user_id FROM user_group WHERE group_id == {group_id}'
    correct2 = executeQuery(query)
    if isinstance(correct2, bool) and not correct2:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail='Internal Server Error'
        )
    if user.get("user_id") not in [id[0] for id in correct1] + [id[0] for id in correct2]:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail='User doesn\'t belong to the group'
        )
    return [id[0] for id in correct2]
