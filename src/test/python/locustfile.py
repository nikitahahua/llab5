from locust import HttpUser, task, between

class WebsiteUser(HttpUser):
    wait_time = between(1, 5)
    host = "http://localhost:8080"

    @task(2)
    def get_page_2(self):
        self.client.get("/page2.html")

    @task(1)
    def get_index_html(self):
        self.client.get("/index.html")