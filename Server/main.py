from fastapi import FastAPI
from models import *


##############################################################
################ utworzenie obiektu aplikacji ################
##############################################################

app = FastAPI(
    title='Grupkowo',
    description='Nowa lepsza wersja facebooka',
    version='0.69.420'
)


##############################################################
########################## end pointy ########################
##############################################################

# default
@app.get('/')
async def f0():
    return {
        'app-name': 'Grupkowo',
        'link': 'https://github.com/KamilMatejuk/Grupkowo'
    }

# /user/login
@app.post(
    '/user/login/',
    tags=['login'],
    summary='Zalogowanie uzytkownika'
)
async def f1(user: RequestLogin):
    return {}

# /user/me
@app.post(
    '/user/me/',
    tags=['aktualny użytkownik'],
    summary='Zarejestrowanie użytkownika'
)
async def f2(user: RequestRegister):
    return {}

@app.get(
    '/user/me/',
    tags=['aktualny użytkownik'],
    summary='Pobranie profilu'
)
async def f3():
    return {}

@app.put(
    '/user/me/',
    tags=['aktualny użytkownik'],
    summary='Edycja profilu'
)
async def f4(user: RequestEditUser):
    return {}

@app.delete(
    '/user/me/',
    tags=['aktualny użytkownik'],
    summary='Usunięcie profilu'
)
async def f5():
    return {}

# /user/groups-user/
@app.get(
    '/user/groups-user/',
    tags=['aktualny użytkownik'],
    summary='Pobranie listy grup do których należy użytkownik'
)
async def f6():
    return {}

# /user/groups-admin/
@app.get(
    '/user/groups-admin/',
    tags=['aktualny użytkownik'],
    summary='Pobranie listy grup, których adminem jest użytkownik'
)
async def f7():
    return {}

@app.post(
    '/user/groups-admin/',
    tags=['aktualny użytkownik'],
    summary='Utworzenie nowej grupy'
)
async def f8(group: RequestCreateGroup):
    return {}

# /user/{user_id}
@app.get(
    '/user/{user_id}/',
    tags=['inny użytkownik'],
    summary='Pobranie profilu innego użytkownika'
)
async def f9(user_id: int):
    return {}

# /group/{group_id}/
@app.get(
    '/group/{group_id}/',
    tags=['grupa'],
    summary='Pobranie danych grupy'
)
async def f10(group_id: int):
    return {}

@app.delete(
    '/group/{group_id}/',
    tags=['grupa'],
    summary='Usunięcie grupy'
)
async def f11(group_id: int):
    return {}

# /group/{group_id}/users/
@app.get(
    '/group/{group_id}/users/',
    tags=['grupa'],
    summary='Pobranie listy użytkowników grupy'
)
async def f12(group_id: int):
    return {}

@app.post(
    '/group/{group_id}/users/',
    tags=['grupa'],
    summary='Dodanie użytkownika do grupy'
)
async def f13(group_id: int, user: RequestAddUser):
    return {}

@app.delete(
    '/group/{group_id}/users/{user_id}',
    tags=['grupa'],
    summary='Usuniecie użytkownika z grupy'
)
async def f14(group_id: int):
    return {}

# /group/{group_id}/posts/
@app.get(
    '/group/{group_id}/posts/',
    tags=['post'],
    summary='Pobranie ostatnich postów w grupie'
)
async def f15(group_id: int):
    return {}

@app.post(
    '/group/{group_id}/posts/',
    tags=['post'],
    summary='Dodanie posta w grupie'
)
async def f16(group_id: int, post: RequestCreatePost):
    return {}

# /group/{group_id}/posts/{timestamp_start}/{timestamp_end}/
@app.get(
    '/group/{group_id}/posts/{timestamp_start}/{timestamp_end}/',
    tags=['post'],
    summary='Pobranie listy postów w grupie z danego zakresu dat'
)
async def f17(group_id: int, timestamp_start: int, timestamp_end: int):
    return {}

# /group/{group_id}/posts/{post_id}/
@app.put(
    '/group/{group_id}/posts/{post_id}/',
    tags=['post'],
    summary='Edycja posta'
)
async def f18(group_id: int, post_id: int, post: RequestCreatePost):
    return {}

@app.delete(
    '/group/{group_id}/posts/{post_id}/',
    tags=['post'],
    summary='Usunięcie posta'
)
async def f19(group_id: int, post_id: int):
    return {}

# /group/{group_id}/posts/{post_id}/reactions/
@app.get(
    '/group/{group_id}/posts/{post_id}/reactions/',
    tags=['post'],
    summary='Pobranie reakcji do posta'
)
async def f20(group_id: int, post_id: int):
    return {}

@app.post(
    '/group/{group_id}/posts/{post_id}/reactions/',
    tags=['post'],
    summary='Dodanie reakcji do posta'
)
async def f21(group_id: int, post_id: int, reaction: RequestReaction):
    return {}

@app.delete(
    '/group/{group_id}/posts/{post_id}/reactions/',
    tags=['post'],
    summary='Usunięcie reakcji do posta'
)
async def f22(group_id: int, post_id: int):
    return {}

# /group/{group_id}/posts/{post_id}/comments/
@app.get(
    '/group/{group_id}/posts/{post_id}/comments/',
    tags=['komentarz'],
    summary='Pobranie komentarzy do posta'
)
async def f23(group_id: int, post_id: int):
    return {}

@app.post(
    '/group/{group_id}/posts/{post_id}/comments/',
    tags=['komentarz'],
    summary='Dodanie komentarza do posta'
)
async def f24(group_id: int, post_id: int, comment: RequestCreateComment):
    return {}

# /group/{group_id}/posts/{post_id}/comments/{comment_id}/
@app.put(
    '/group/{group_id}/posts/{post_id}/comments/{comment_id}/',
    tags=['komentarz'],
    summary='Edycja komentarza pod postem'
)
async def f25(group_id: int, post_id: int, comment_id: int, comment: RequestCreateComment):
    return {}

@app.delete(
    '/group/{group_id}/posts/{post_id}/comments/{comment_id}/',
    tags=['komentarz'],
    summary='Usunięcie komentarza pod postem'
)
async def f26(group_id: int, post_id: int, comment_id: int):
    return {}

# /group/{group_id}/chats/
@app.get(
    '/group/{group_id}/chats/',
    tags=['chat'],
    summary='Pobranie ostatnich wiadomości'
)
async def f27(group_id: int):
    return {}

@app.post(
    '/group/{group_id}/chats/',
    tags=['chat'],
    summary='Wysłanie wiadomości'
)
async def f28(group_id: int, message: RequestCreateMessage):
    return {}

# /group/{group_id}/chats/{timestamp_start}/{timestamp_end}/
@app.get(
    '/group/{group_id}/chats/{timestamp_start}/{timestamp_end}/',
    tags=['chat'],
    summary='Pobranie wiadomości z danego zakresu dat'
)
async def f29(group_id: int, timestamp_start: int, timestamp_end: int):
    return {}

# /group/{group_id}/chats/{message_id}/reactions/
@app.get(
    '/group/{group_id}/chats/{message_id}/reactions/',
    tags=['chat'],
    summary='Pobranie reakcji do danej wiadomości'
)
async def f30(group_id: int, message_id: int):
    return {}

@app.post(
    '/group/{group_id}/chats/{message_id}/reactions/',
    tags=['chat'],
    summary='Zareagowanie na wiadomość'
)
async def f31(group_id: int, message_id: int, reaction: RequestReaction):
    return {}

@app.delete(
    '/group/{group_id}/chats/{message_id}/reactions/',
    tags=['chat'],
    summary='Usunięcie reakcji na wiadomość'
)
async def f32(group_id: int, message_id: int):
    return {}
