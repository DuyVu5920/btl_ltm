# Game giải toán đối kháng
## Công việc của mỗi thành viên:

**Vũ Trọng Duy - B18DCCN114:**
- thiết kế GUI phần gameplay và login/register
- tạo kết nối server-client và kết nối database
- code logic game

## Phân tích yêu cầu ứng dụng
- Hệ thống có một server và nhiều client. Server lưu toàn bộ thông tin và dữ liệu.
- Để chơi, người chơi phải login vào tài khoản của mình từ một máy client. Sau khi login thành công, giao diện hiện lên một danh sách các người chơi đang online, mỗi người chơi có các thông tin: tên, tổng số điểm hiện có của người chơi, trạng thái (hoặc đang bận nếu đang chơi với người khác, hoặc đang rỗi nếu không chơi với ai).
- Ngoài ra tại màn hình này còn có chức năng bảng xếp hạng Leaderboard, chi tiết bảng xếp hạng được nêu dưới cùng
- Có thể xem chi tiết người chơi bằng cách chọn người chơi trong danh sách và bấm vào phần Info
- Muốn muốn chat với ai thì người chơi click vào tên của đối thủ đó trong danh sách online và chọn chức năng Message
- Khi được mời chat, người chơi có thể chấp nhận (OK), hoặc từ chối (Reject).
- Muốn mời (thách đấu) ai thì người chơi click vào tên của đối thủ đó trong danh sách online và chọn chức năng Play
- Khi bị thách đấu, người chơi có thể chấp nhận (OK), hoặc từ chối (Reject).
- Khi chấp nhận, 2 người chơi sẽ vào chơi với nhau, và server sẽ làm trọng tài. Giao diện chơi gồm một danh sách N câu hỏi toán nhanh theo dạng trắc nghiệm, ô thời gian và nút thoát.
- Server sẽ sinh tự động cùng một đề bài và gửi về cho cả hai đối thủ chơi. Mỗi người chơi phải click chọn các đáp án của các câu hỏi. Khi hoàn thành thì click nút submit bài làm. Ai đúng hết và nhanh hơn thì giành chiến thắng. Nếu cả hai đều có ít nhất 1 câu sai thì hòa.
- Sau mỗi trận, server sẽ kiểm tra xem ai thắng và gửi kết quả về cho cả 2 đối thủ: thắng 1 điểm, hòa 0.5 điểm, thua 0 điểm.
- Sau mỗi ván, đều có dialog hỏi mỗi người chơi có muốn tiếp tục không. Nếu cả hai tiếp tục thì chơi tiếp, nếu một trong hai đối thủ dừng chơi thì thoát ra và server báo cho người chơi còn lại.
- Kết quả các trận đấu được lưu vào server. Mỗi người chơi đều có thể vào xem bảng xếp hạng các người chơi trong toàn bộ hệ thống, theo lần lượt các tiêu chí: tổng số điểm (giảm dần), trung bình điểm của các đối thủ đã gặp (giảm dần), trung bình thời gian kết thúc trong các trận thắng (tăng dần).

## Techstack
- **GUI**: java swing
- **TCP/IP**: java Socket/ServerSocket
- **Database connection**: JDBC/mySQL Driver
- **Muiti-threaded programming**: ThreadPoolExecutor
